#! /usr/bin/env groovy

import com.lab.build.Colorizer

/**
 * Jenkins pipeline build whole project
 *
 * @return Return value
 *
 * @version $Id$
 * @copyright 2018, EPAM systems. All Rights Reserved
 */

@Library(value='initialLibs', changelog=false) _

node ("master") {
    wrap([$class: 'TimestamperBuildWrapper']) {
        env.BUILD_STATUS = 'SUCCESS'
        echo Colorizer.info("Executing Checkout stage")
        gitCheckout {
            BranchName =  '*/master'
            SubmoduleConfig = false
            CredentialsID = '6da246df-c194-4f83-bdfa-9edee7ca39a2'
            GitRepository = 'https://github.com/Sanwel/JavaApp'
        }

        def selectedJdk = "Oracle JDK 8"
        def selectedMaven = "maven"
	echo Colorizer.info("Executing maven stage")
        stageMavenExec {
            jdkVersion   = selectedJdk
            mavenVersion = selectedMaven
            jvmOptions   = '-Xms768m -Xmx768m'
            mavenCommand = "mvn clean install"
        }

        echo Colorizer.info("Executing Sonar Scanner stage")
        SonarScaner {
            SonarName = 'SonarQube'
            SonarHome = tool name: 'sonarscanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
            SonarKey = "-Dsonar.projectKey='Simple-App'"
            SonarProj = "-Dsonar.projectName='Simple-App'"
            Sonarbinaries = "-Dsonar.java.binaries='target/classes/'"
            SonarSource = "-Dsonar.sources='src/'"
        }   

        echo Colorizer.info("Executing Dockerize stage")
        DockerizeStage {
            DockerName = 'Docker'
            DockerImageName = 'java_app:Build_'
            DockerFrom = '8080'
            DockerTo = '8181'
            DockerContainerName = 'Olen'
        }

        echo Colorizer.info("Executing Docker Check stage")
        String response
        response = DockerCheckStage {
            TimeOutCheck = 15
            ApplicationIP = ' http://10.28.12.209:8181/health'
        }

        echo Colorizer.info("Executing Send Email stage")
        MailStage {
            Check = response
            Recipient = 'Maksym_Husak@epam.com'
        }

        echo Colorizer.info("Executing CleanUp stage")
        cleanUp {
            DockerContainerName = 'Olen'
            DockerImageName = 'java_app:Build_'
        }
    }
}
