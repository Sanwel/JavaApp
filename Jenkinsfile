#! /usr/bin/env groovy

node {
    //declaration variables
    def BranchName = '*/master'
    def GitRepository = 'https://github.com/Sanwel/JavaApp'
    def CredentialsId = '6da246df-c194-4f83-bdfa-9edee7ca39a2'
    def Response
    def SonarKey = 'Simple-App'
    def SonarBinariesPath = 'target/classes/'
    def SonarName = 'SonarQube'
    def SonarScannerName = 'sonarscanner'
    def MavenName = 'maven'
    def DockerName = 'Docker'
    def DockerFrom = '8080'
    def DockerTo = '8181'
    def DockerContainerName = 'Olen'
    def DockerImageName = 'java_app:Build_'
    def ApplicationIP = 'http://10.28.12.209:8181/health'
    def SonarSource = 'src/'
    def TimeOutCheck = 15
    def LastBuild = 1
    def Int = env.BUILD_ID.toInteger().minus(LastBuild)
    def sonarHome = tool name: SonarScannerName, type: 'hudson.plugins.sonar.SonarRunnerInstallation'
    def MavenTargets = 'clean install'
    String Recipient ='Maksym_Husak@epam.com'
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
                withMaven ( maven: MavenName ) {
                    sh "mvn ${MavenTargets}"
                }
                BUILD_STATUS = 'SUCCESS'
            }
            //Sonar Analyzing stage
            stage ('SonarQube testing') {
                echo 'SonarQube Test'
                withSonarQubeEnv(SonarName) {
                    sh "${sonarHome}/bin/sonar-scanner -Dsonar.projectKey=${SonarKey} -Dsonar.projectName=${SonarKey} -Dsonar.java.binaries=${SonarBinariesPath}  -Dsonar.sources=${SonarSource}"
                }
            }
            //Dockerize Application and run it
            stage ('Dockerize') {
                echo 'Run Application in Docker'
                docker.withTool(DockerName) {
                    docker.build("${DockerImageName}${env.BUILD_ID}","-f Dockerfile ./")
                    docker.image("${DockerImageName}${env.BUILD_ID}").run(" -p ${DockerTo}:${DockerFrom} --name ${DockerContainerName}")
                }
            }
            // Checking successful dockerize stage
            timeout (time: TimeOutCheck, unit:'SECONDS') { 
                stage('Docker Check') {
                    echo 'Check Successful docker container Up'
                    sleep 10
                    while(Response!="HTTP/1.1 200") {
                        def Curl = "curl -I ${ApplicationIP}".execute().text.
                        println Curl
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
            stage('CleanUp') {
                sh "docker rm -f ${DockerContainerName}"
                sh "docker rmi ${DockerImageName}${Int} > /dev/null 2>&1"
                sh "git clean -ffdx"
            }
        }
}