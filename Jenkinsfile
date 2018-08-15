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
// define variables
env.BUILD_STATUS = 'SUCCESS'
String response
def selectedJdk = 'Oracle JDK 8'
def selectedMaven = 'maven'
String gitJavaRepo = 'https://github.com/Sanwel/JavaApp'
// Please add human readable creds id in jenkins config and here
String credsJavaRepo   = '6da246df-c194-4f83-bdfa-9edee7ca39a2'
String gitJavaBranch = '*/master'
String sonarQubeName = 'SonarQube'
String sonarPrj = 'Simple-App'
String sonarSrc = 'src/'
String sonarJavaBins = 'target/classes/'

    
    wrap([$class: 'AnsiColorBuildWrapper']) {
        try {
            echo Colorizer.info("Executing Checkout stage")
            stageGitCheckout {
                gitRepository = gitJavaRepo
                credentialsID = credsJavaRepo
                branchName =  gitJavaBranch
                // I think this param "submoduleConfig" is not needed. Please remove it from here and hardcode it in libs
                submoduleConfig = false
                // You can add checkout subdirectory if you want
            }

            echo Colorizer.info("Executing maven stage")
            stageMavenBuild {
                jdkVersion   = selectedJdk
                mavenVersion = selectedMaven
                jvmOptions   = '-Xms768m -Xmx768m'
                mavenCommand = "mvn clean install"
            }

            echo Colorizer.info("Executing Sonar Scanner stage")
            stageSonarScaner {
                sonarName = sonarQubeName
                // is this working? not sure.
                sonarHome = tool name: 'sonarscanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                // please hide parameters into lib and pass only values. -Dsonar.projectKey= -Dsonar.projectName= not needed here
                sonarKey = "-Dsonar.projectKey='${sonarPrj}'"
                sonarProj = "-Dsonar.projectName='${Simple-App}'"
                sonarbinaries = "-Dsonar.java.binaries='${sonarJavaBins}'"
                sonarSource = "-Dsonar.sources='${sonarSrc}'"
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
