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
env.BUILD_STATUS = 'SUCCESS'
String response
    try {
        wrap([$class: 'AnsiColorBuildWrapper']) { 
            echo Colorizer.info("Executing Checkout stage")
            stageGitCheckout {
                BranchName =  '*/master'
                SubmoduleConfig = false
                CredentialsID = '6da246df-c194-4f83-bdfa-9edee7ca39a2'
                GitRepository = 'https://github.com/Sanwel/JavaApp'
            }

            def selectedJdk = "Oracle JDK 8"
            def selectedMaven = "maven"
            echo Colorizer.info("Executing maven stage")
            stageMavenBuild {
                jdkVersion   = selectedJdk
                mavenVersion = selectedMaven
                jvmOptions   = '-Xms768m -Xmx768m'
                mavenCommand = "mvn clean install"
            }

            echo Colorizer.info("Executing Sonar Scanner stage")
            stageSonarScaner {
                SonarName = 'SonarQube'
                SonarHome = tool name: 'sonarscanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                SonarKey = "-Dsonar.projectKey='Simple-App'"
                SonarProj = "-Dsonar.projectName='Simple-App'"
                Sonarbinaries = "-Dsonar.java.binaries='target/classes/'"
                SonarSource = "-Dsonar.sources='src/'"
            }   

            echo Colorizer.info("Executing Dockerize stage")
            stageDockerize {
                DockerName = 'Docker'
                DockerImageName = 'java_app:Build_'
                DockerPortOutbound = '8080'
                DockerPortInbound = '8181'
                DockerContainerName = 'Olen'
            }

            echo Colorizer.info("Executing Docker Check stage")
            response = stageDockerCheck {
                TimeOutCheck = 15
                ApplicationIP = ' http://10.28.12.209:8181/health'
            }

            echo Colorizer.info("Executing Send Email stage")
            stageEmail {
                Check = response
                Recipient = 'Maksym_Husak@epam.com'
            }
        }
    }
    catch (all) {
        echo Colorizer.info('Catch Errors')
        currentBuild.result = 'FAILURE'
        env.BUILD_STATUS = 'FAILURE'
        emailext(subject: "${env.JOB_NAME} was ${env.BUILD_STATUS}", body: "Commit short hash " + "${env.shortCommit}", to: 'Maksym_Husak@epam.com', replyTo: '');
    }
    finally {
        echo Colorizer.info('Executing CleanUp Stage')
        stageCleanUp {
            DockerContainerName = 'Olen'
            DockerImageName = 'java_app:Build_'
        }
    }
}
