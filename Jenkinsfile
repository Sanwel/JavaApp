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
    wrap([$class: 'AnsiColorBuildWrapper']) {
        try {
            echo Colorizer.info("Executing Checkout stage")
            stageGitCheckout {
                branchName =  '*/master'
                submoduleConfig = false
                credentialsID = '6da246df-c194-4f83-bdfa-9edee7ca39a2'
                gitRepository = 'https://github.com/Sanwel/JavaApp'
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
                sonarName = 'SonarQube'
                sonarHome = tool name: 'sonarscanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                sonarKey = "-Dsonar.projectKey='Simple-App'"
                sonarProj = "-Dsonar.projectName='Simple-App'"
                sonarbinaries = "-Dsonar.java.binaries='target/classes/'"
                sonarSource = "-Dsonar.sources='src/'"
            }   

            echo Colorizer.info("Executing Dockerize stage")
            stageDockerize {
                dockerName = 'Docker'
                dockerImageName = 'java_app:Build_'
                dockerPortInbound = '8080'
                dockerPortOutbound = '8181'
                dockerContainerName = 'Olen'
            }

            echo Colorizer.info("Executing Docker Check stage")
            response = stageDockerCheck {
                timeOutCheck = 15
                applicationIP = ' http://10.28.12.209:8181/health'
            }

            echo Colorizer.info("Executing Send Email stage")
            stageEmail {
                check = response
                recipient = 'Maksym_Husak@epam.com'
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
                dockerContainerName = 'Olen'
                dockerImageName = 'java_app:Build_'
            }
        }
    }
}
