#! /usr/bin/env groovy

node {
    //declaration variables
    def BranchName = '*/master'
    def GitRepository = 'https://github.com/Sanwel/JavaApp'
    def CredentialsId = '6da246df-c194-4f83-bdfa-9edee7ca39a2'
    def Response
    def LastBuild = 1
    def Int = env.BUILD_ID.toInteger().minus(LastBuild)
    def sonarHome = tool name: 'sonarscanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
    String Recipient ="Maksym_Husak@epam.com"
        try{
            //Git-Checkout stage with getting commit hash
            stage('Git-Checkout') {
                echo 'Git Checkout'
                checkout([$class: 'GitSCM', branches: [[name: BranchName]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: CredentialsId , url: GitRepository ]]])
                shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:\'%h\'").trim()
            }
            //maven Build stage
            stage ('Build') {
                echo 'Maven Build'
                withMaven ( maven: 'maven' ) {
                    sh "mvn clean install"
                }
                BUILD_STATUS = "SUCCESS"
            }
            //Sonar Analyzing stage
            stage ('SonarQube testing') {
                echo 'SonarQube Test'
                withSonarQubeEnv('SonarQube') {
                    sh "${sonarHome}/bin/sonar-scanner -Dsonar.projectKey=Simple-App -Dsonar.projectName=Simple-App -Dsonar.java.binaries=target/classes/  -Dsonar.sources=src/"
                }
            }
            //Dockerize Application and run it
            stage ('Dockerize') {
                echo 'Run Application in Docker'
                docker.withTool('Docker') {
                    docker.build("java_app:Build_${env.BUILD_ID}","-f Dockerfile ./")
                    docker.image("java_app:Build_${env.BUILD_ID}").run('-p 8181:8080 --name Olen')
                }
            }
            // Checking successful dockerize stage
            timeout (time: 15, unit:'SECONDS') { 
                stage('Docker Check') {
                    echo 'Check Successful docker container Up'
                    sleep 5
                    while(Response!="HTTP/1.1 200") {
                        def Curl = "curl -I http://10.28.12.209:8181/health".execute().text
                        Response = Curl[0..11]
                    }
                }    
            }
            //Notification stage by using emailExtendingPlugin
            stage('Mail'){
                echo 'Send email notification'
                if(Response.equals("HTTP/1.1 200")) {
                    emailext(subject: "${env.JOB_NAME} was " + "${BUILD_STATUS}", body: "Commit short hash " + "${shortCommit}", to: Recipient, replyTo: '');
                }else {
                      System.exit(1)
                }
            }           
        }
        //Catching errors stage and notify about fail by email 
        catch (all) {
            echo 'Catch Errors'
            currentBuild.result = 'FAILURE'
            BUILD_STATUS = "FAILURE"
            emailext(subject: "${env.JOB_NAME} was ${BUILD_STATUS}", body: "Commit short hash " + "${shortCommit}", to: Recipient, replyTo: '');    
        }
        //Clean up 
        finally {
            sh 'docker rm -f Olen'
            sh "docker rmi java_app:Build_${Int} > /dev/null 2>&1"
            sh 'git clean -ffdx'
        }
}