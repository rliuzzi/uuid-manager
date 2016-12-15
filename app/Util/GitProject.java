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

    public static final String ROOT_DIR = "$HOME/git/uuid-store";
    private static final String GIT_REPO_URL = "git@github.com:rliuzzi/uuid-store.git";
    private File localPath;
    private Git gitProject;
    private TransportConfigCallback transportConfigCallback;

    private String branch = "master";

    public File getLocalPath() {
        return localPath;
    }

    @Inject
    public GitProject() throws IOException, GitAPIException{
        if(Play.current().isProd()) {
            cloneRemoteRepository(branch);
            checkoutBranch(branch);
        } else {
            localPath = new File(ROOT_DIR);
            gitProject = Git.open(localPath);
        }
        gitProject.pull().setTransportConfigCallback(transportConfigCallback).call();
    }

    public void cloneRemoteRepository(String branch) throws IOException, GitAPIException {

        Files.createDirectories(Paths.get(ROOT_DIR));
        File localAndroidRepo = new File(ROOT_DIR);

        ArrayList<String> branchesToClone = new ArrayList<String>();
        branchesToClone.add("master");
        branchesToClone.add(branch);

        // then clone
        System.out.println("Cloning from " + GIT_REPO_URL + " to " + ROOT_DIR);

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

        gitProject = Git.cloneRepository()
                .setURI(GIT_REPO_URL)
                .setDirectory(localAndroidRepo)
                .setBranchesToClone(branchesToClone)
                .setTransportConfigCallback(transportConfigCallback)
                .call();

        System.out.println("Having repository: " + gitProject.getRepository().getDirectory());
    }


    public void checkoutBranch(String branch) throws IOException, GitAPIException{
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
        gitProject.pull().setTransportConfigCallback(transportConfigCallback).call();
        System.out.println("git add file: " + nameFileToAdd);
        gitProject.add().addFilepattern(nameFileToAdd).call();
        CommitCommand commit = gitProject.commit();
        commit.setMessage(message).call();
        gitProject.push().setTransportConfigCallback(transportConfigCallback).call();
    }

    public void addAllCommitPush(String message) throws IOException, GitAPIException {
        gitProject.pull().setTransportConfigCallback(transportConfigCallback).call();
        System.out.println("git add all files ");
        gitProject.add().addFilepattern(".").call();
        CommitCommand commit = gitProject.commit();
        commit.setMessage(message).call();
        gitProject.push().setTransportConfigCallback(transportConfigCallback).call();
    }

}
