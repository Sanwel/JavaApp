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
    def gitCredential = 'gitHub'
    def directoryForCheckout = './'
    def selectedBranchName = '*/master'
    def selectedRepository = 'https://github.com/Sanwel/JavaApp'
    def selectedMaven = 'maven'
    def currentMavenCommand = 'mvn clean install'
    def currentJvmOptions = '-Xms768m -Xmx768m'
    def selectedJdk = 'Oracle JDK 8'
    def selectedSonarName = 'SonarQube'
    def selectedSonarScanner = 'sonarscanner'
    def currentSonarKey = 'Simple-App'
    def pathToSonarBinaries = 'target/classes/'
    def pathToSonarSource = 'src/'
    def selectedDocker = 'Docker'
    def currentDockerImagName = 'java_app:Byild_'
    def dockerPortIn = '8080'
    def dockerProtOut = '8181'
    def currentDockerConatinerName = 'Olen'
    def currentTimeOut = 15 // Which units of time is use ?
    def currentAppIP = ' http://10.28.12.209:8181/health'
    def currentRecipient = 'Maksym_Husak@epam.com'
    
    wrap([$class: 'AnsiColorBuildWrapper']) {
        try {
            echo Colorizer.info("Executing Checkout stage")
            stageGitCheckout {
                remoteDir = direcotyForCheckout
                branchName =  selectedBranchName
                credentialsID = gitCredential
                gitRepository = selectedRepository
            }
            echo Colorizer.info("Executing maven stage")
            stageMavenBuild {
                jdkVersion   = selectedJdk
                mavenVersion = selectedMaven
                jvmOptions   = currentJvmOptions
                mavenCommand = currentMavenCommand
            }

            echo Colorizer.info("Executing Sonar Scanner stage")
            stageSonarScaner {
                sonarName = selectedSonarName
                sonarHome = tool name: selectedSonarScanner, type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                sonarKey = currentSonarKey
                sonarProj = currentSonarKey
                sonarbinaries = pathToSonarBinaries
                sonarSource = pathToSonarSource
            }   

            echo Colorizer.info("Executing Dockerize stage")
            stageDockerize {
                dockerName = selectedDocker
                dockerImageName = currentDockerImagName
                dockerPortInbound = dockerPortIn
                dockerPortOutbound = dockerProtOut
                dockerContainerName = currentDockerConatinerName
            }

            echo Colorizer.info("Executing Docker Check stage")
            response = stageDockerCheck {
                timeOutCheck = currentTimeOut
                applicationIP = currentAppIP
            }

            echo Colorizer.info("Executing Send Email stage")
            stageEmail {
                check = response
                recipient = currentRecipient
            }
        }
        catch (all) {
            echo Colorizer.info('Catch Errors')
            currentBuild.result = 'FAILURE'
            env.BUILD_STATUS = 'FAILURE'
            email   ext(subject: "${env.JOB_NAME} was ${env.BUILD_STATUS}", body: "Commit short hash " + "${env.shortCommit}", to: currentRecipient, replyTo: '');
        }
        finally {
            echo Colorizer.info('Executing CleanUp Stage')
            stageCleanUp {
                dockerContainerName = currentDockerConatinerName
                dockerImageName = currentDockerImagName
            }
        }
    }
}
