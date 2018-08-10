node {
def BranchName = '*/master'
def GitRepository = 'https://github.com/Sanwel/JavaApp'
def CredentialsId = '6da246df-c194-4f83-bdfa-9edee7ca39a2'
def Response
def LastBuild = 5
def sonarHome = tool name: 'sonarscanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
String Recipient ="Maksym_Husak@epam.com"
        try{
            stage('Git-Checkout') {
                echo 'Git Checkout'
                checkout([$class: 'GitSCM', branches: [[name: BranchName]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: CredentialsId , url: GitRepository ]]])
                shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:\'%h\'").trim()
            }
            stage ('Build') {
                echo 'Maven Build'
                withMaven (
                    maven: 'maven'
                ) {
                    sh "mvn clean install"
                }
                BUILD_STATUS = "SUCCESS"
            }
            stage ('SonarQube testing') {
                echo 'SonarQube Test'
                withSonarQubeEnv('SonarQube') {
                    sh "${sonarHome}/bin/sonar-scanner -Dsonar.projectKey=Simple-App -Dsonar.projectName=Simple-App -Dsonar.java.binaries=target/classes/  -Dsonar.sources=src/"
                }
            }
            stage ('Dockerize') {
                echo 'Run Application in Docker'
                docker.withTool('Docker') {
                    docker.build("java_app:Build_${env.BUILD_ID}","-f Dockerfile ./")
                    docker.image("java_app:Build_${env.BUILD_ID}").run('-p 8181:8080 --name Olen')
                }
            }
            timeout (time: 15, unit:'SECONDS') { 
                stage('Docker Check') {
                    echo 'Check Successful docker container Up'
                    Delete = "${env.BUILD_ID}" - LastBuild
                    println Delete
                    sleep 5
                    while(Response!="HTTP/1.1 200") {
                        def Curl = "curl -I http://10.28.12.209:8181/health".execute().text
                        Response = Curl[0..11]
                    }
                }    
            }
            stage('Mail'){
                echo 'Send email notification'
                if(Response.equals("HTTP/1.1 200")) {
                    emailext(subject: "${env.JOB_NAME} was " + "${BUILD_STATUS}", body: "Commit short hash " + "${shortCommit}", to: Recipient, replyTo: '');
                }else {
                      System.exit(1)
                }
            }           
} catch (all) {
    echo 'Catch Errors'
    currentBuild.result = 'FAILURE'
    BUILD_STATUS = "FAILURE"
    emailext(subject: "${env.JOB_NAME} was ${BUILD_STATUS}", body: "Commit short hash " + "${shortCommit}", to: Recipient, replyTo: '');    
} finally {
    sh 'docker rm -f Olen'
   // sh "docker rmi java_app:Build_${env.BUILD_ID - 5}"
}
}
