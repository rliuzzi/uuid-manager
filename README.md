# UUID-MANAGER
Demo project to offer an UI on top of fastlane. The purpose of this project is to:

- Allow updates to an uuid devices store
- Allow updates to a certificates store
- Call a fastlane to automate the rest of the publishing and distributing process


# HOW IT WORKS
When the docker container is started all necessary repositories are cloned and the latest changes are pulled to the /tmp/ directory in a docker container.

This project reads and writes uuid files:
- zone-uuids.txt
- consumer-uuids.txt
- driver-uuids.txt

In a Repository such: [PUBLIC EXAMPLE](https://github.com/rliuzzi/uuid-store)

When a new device is added, the app executes a script located in: 
```
/tmp/{$PROJECT_REPO}/fastlane
```

The fastlane/Fastfile script add_devices:

1. Reads the uuids.txt file
2. Adds the uuids to the app certificate
3. Downloads the certificate
4. Regenerates the the .ipa with the new certificate
5. Publishes the generated .ipa to Hockeyapps
6. Posts a notification to a Slack channel to notify the app is available for download

The script is executed like:
```
fastlane add_devices deviceFile:/tmp/git/iOS-uuid/{$CONTEXT}-uuids.txt
```
Your script [/tmp/{$PROJECT_REPO}/fastlane/Fastfile](https://github.com/fastlane/fastlane/tree/master/fastlane/docs) should look something like:
```
fastlane_version "1.111.0"

default_platform :ios

platform :ios do
  before_all do |lane, options|
    # ENV["SLACK_URL"] = "https://hooks.slack.com/services/..."
    #cocoapods

  end

  desc "Runs all the tests"
  lane :test do 
    scan
  end

  lane :add_devices do |options|
    register_devices(devices_file: options[:deviceFile])
    match(type: "adhoc", force_for_new_devices: true,
    username: "YOUR USER NAME HERE",
    git_url:"GITHUB REPOSITORY TO STORE CERTS",
    git_branch:”YOUR BRANCH”,
    app_identifier: "YOUR APP IDENTIFIER")

    gym(
       use_legacy_build_api: true,
       workspace: “YOURWORKSPACE.xcworkspace”,
       configuration: “Debug”,
       scheme: “YOUR SCHEME”,
       silent: true,
       clean: true,
      #  output_directory: “../CrashlyticsBetaBuilds”, # Destination directory. Defaults to current directory.
    )

    hockey(
      api_token: "YOUR TOKEN",
      public_identifier: ENV['PUBLIC ID'],
      ipa: 'ROUTE TO YOUR .IPA FILE',
      # notes_type: markdown_type,
      # notes: release_notes,
    )

    ENV["SLACK_URL"] = "YOUR SLACK CHANNEL URL"

    slack(
      message: "New UDID's added to PROJECT. Please go to LINK TO HOCKEY APP HERE to download"
    )
  end

  # You can define as many lanes as you want

  after_all do |lane, options|
    # This block is called, only if the executed lane was successful

    # slack(
    #   message: "Successfully deployed new App Update."
    # )
  end

  error do |lane, exception, options|
    # slack(
    #   message: exception.message,
    #   success: false
    # )
  end
end

```

# SETUP:
- Clone the project:
```
cd $HOME/git/
git clone git@github.com:rliuzzi/uuid-manager.git
```

- If you haven't already done so, [install Java 8 JDK] (https://java.com/en/download/)

### Change files to point to your project keeping in mind there is "Context" idea:
- You can change the enum Context file to use your project contexts.
- GitProject should point to your repos. 
- CmdExecutor constants should point to your faslane scripts home
- The menu: main.scala.html should pass on the right context to the getDevices function.

**OPTIONAL STEP** If you need this application to access a private repository you will need to add your ssh keys to:
```
target/docker/stage/opt/ghost-dev/.ssh/
```
Check how to generate this keys [HERE] (https://help.github.com/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/) 

# RUN:

- If you haven't done so already, [install Activator] (https://www.lightbend.com/activator/download)
- Run activator
```
$INSTALATION_HOME/activator/bin/activator
```

- Select the app to run from the directory
- Compile, build and Run
- Open a browser to http://IP:9000

