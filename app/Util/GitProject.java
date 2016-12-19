package Util;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;
import play.api.Play;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by rominaliuzzi on 15/12/2016.
 */
public class GitProject {

//    public static final String ROOT_DIR_UUID_STORE = System.getProperty("user.home") + "/git/iOS-uuid";
    public static final String ROOT_DIR_UUID_STORE = "/tmp/git/iOS-uuid";
    public static final String GIT_REPO_URL_UUID_STORE = "git@github.com:coolnagour/iOS-uuid";
    private static final String GIT_REPO_URL_ZONE_CONTROLLER = "git@github.com:coolnagour/App-iOS-Controller";
    public static final String ROOT_DIR_UUID_ZONE_CONTROLLER = "/tmp/git/App-iOS-Controller";
    private Git gitProjectUuidStore;
    private Git gitProjectZoneController;
    private TransportConfigCallback transportConfigCallback;

    private String branch = "master";


    @Inject
    public GitProject() throws IOException, GitAPIException{

        gitProjectUuidStore = cloneRemoteRepository(GIT_REPO_URL_UUID_STORE, ROOT_DIR_UUID_STORE, branch);
        checkoutBranch(gitProjectUuidStore, branch);
        gitProjectUuidStore.pull().setTransportConfigCallback(transportConfigCallback).call();

        gitProjectZoneController = cloneRemoteRepository(GIT_REPO_URL_ZONE_CONTROLLER, ROOT_DIR_UUID_ZONE_CONTROLLER, branch);
        checkoutBranch(gitProjectZoneController, branch);
        gitProjectZoneController.pull().setTransportConfigCallback(transportConfigCallback).call();

        //TODO: Add Missing Repos
    }

    public Git cloneRemoteRepository(String repoUrl, String rootDir, String branch) throws IOException, GitAPIException {

        Files.createDirectories(Paths.get(rootDir));
        File localAndroidRepo = new File(rootDir);

        ArrayList<String> branchesToClone = new ArrayList<String>();
        branchesToClone.add("master");
        branchesToClone.add(branch);

        // then clone
        System.out.println("Cloning from " + repoUrl + " to " + rootDir);

        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session ) {
                session.setConfig("StrictHostKeyChecking", "no");
            }

            @Override
            protected JSch createDefaultJSch(FS fs ) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch( fs );
                defaultJSch.addIdentity( "/opt/ghost-dev/.ssh/id_rsa" );
                return defaultJSch;
            }

        };

        transportConfigCallback = new TransportConfigCallback() {
            @Override
            public void configure( Transport transport ) {
                SshTransport sshTransport = ( SshTransport )transport;
                sshTransport.setSshSessionFactory( sshSessionFactory );
            }
        };

        Git gitProject = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(localAndroidRepo)
                .setBranchesToClone(branchesToClone)
                .setTransportConfigCallback(transportConfigCallback)
                .call();

        System.out.println("Having repository: " + gitProject.getRepository().getDirectory());

        return gitProject;
    }


    public void checkoutBranch(Git gitProject, String branch) throws IOException, GitAPIException{
        System.out.println("Checkingout branch " + branch);
        Ref ref;
        try {
            ref = gitProject.checkout().
                    setCreateBranch(true).
                    setName(branch).
                    setForce(true).
                    setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM).
                    setStartPoint("origin/" + branch).
                    call();
        } catch (RefAlreadyExistsException e) {
            ref = gitProject.checkout().
                    //setCreateBranch(true).
                            setName(branch).
                            setForce(true).
                            setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM).
                            setStartPoint("origin/" + branch).
                            call();
        }
        System.out.println("Currently at branch REF" + ref.getName());
        System.out.println("Currently at branch " + gitProject.getRepository().getFullBranch());

    }

    public void addCommitPush(String nameFileToAdd, String message) throws IOException, GitAPIException {
        //PULL LATEST CHANGES ON ALL REPOS
        gitProjectUuidStore.pull().setTransportConfigCallback(transportConfigCallback).call();
        gitProjectZoneController.pull().setTransportConfigCallback(transportConfigCallback).call();
        //TODO: Add Missing Repos

        System.out.println("git add file: " + nameFileToAdd);
        gitProjectUuidStore.add().addFilepattern(nameFileToAdd).call();
        CommitCommand commit = gitProjectUuidStore.commit();
        commit.setMessage(message).call();
        gitProjectUuidStore.push().setTransportConfigCallback(transportConfigCallback).call();
    }

    public void addAllCommitPush(String message) throws IOException, GitAPIException {
        //PULL LATEST CHANGES ON ALL REPOS
        gitProjectUuidStore.pull().setTransportConfigCallback(transportConfigCallback).call();
        gitProjectZoneController.pull().setTransportConfigCallback(transportConfigCallback).call();
        //TODO: Add Missing Repos

        System.out.println("git add all files ");
        gitProjectUuidStore.add().addFilepattern(".").call();
        //Only commit and push in RELEASE MODE
        if(Play.current().isProd()) {
            CommitCommand commit = gitProjectUuidStore.commit();
            commit.setMessage(message).call();
            gitProjectUuidStore.push().setTransportConfigCallback(transportConfigCallback).call();
        }
    }



}
