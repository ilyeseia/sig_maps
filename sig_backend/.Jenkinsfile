pipeline{
    agent{
        label 'sig-node'
    }
    options { buildDiscarder(logRotator(numToKeepStr: '30',artifactDaysToKeepStr: '30', artifactNumToKeepStr: '30', daysToKeepStr: '10')) }
    environment {
        BACKEND_IMAGE_TAG = "sig/backend:build-${env.BUILD_ID}"
    }
    stages{
        stage("Copy Custom Files"){
            steps{
                echo "======== copy templates ========"
    //            sh "sed -i '/org.flywaydb/d' ${WORKSPACE}/build.gradle"
                sh "cp /home/tmps/backend/global.properties ${WORKSPACE}/src/main/resources/"
                sh "cp /home/tmps/backend/application.properties ${WORKSPACE}/src/main/resources/"
            }
        }
    //    stage('Sonarqube Code analysis'){
         //       steps{
           //          withSonarQubeEnv('sonarqube-server') {
              //          sh './gradlew --info sonarqube -Dsonar.projectKey=sig-back -Dsonar.projectName=sig-back'
               //     }
              // }
       // }
        stage("Build and Launch the Backend"){
            steps{
                echo "========executing Build Backend Image========"
                sh "./gradlew bootJar"
                sh "docker build -t ${BACKEND_IMAGE_TAG} ."
                sh "docker stop prodsig && docker rm prodsig || true"
                sh "docker  run   --name prodsig --link postgis:postgis --link redis:redis -p 8585:8080 -d -v /home/tmps/images:/images ${BACKEND_IMAGE_TAG}"
                //sh "docker cp /home/tmps/backend/default.png prodsig:/images"
                //sh "docker cp /home/tmps/backend/map.png prodsig:/images"
            }

        }
    }
    post{
        success{
            sh "docker image rm sig/backend:build-${currentBuild.previousBuild.getNumber()} || true"
        }
        failure{
            mail to: 'hamza.achi@eadn.dz,halim.amrani@eadn.dz,abdenour.achrouf@eadn.dz,ameur.lamour@eadn.dz', subject: "PIPELINE FAILURE ${currentBuild.fullDisplayName}", body: "Boom, build failed :( ${env.BUILD_URL}"
            slackSend(color: 'danger', channel: 'git_sig_events', message: "Build failed :( . JOB: ${env.JOB_NAME} URL: ${env.BUILD_URL}")
        }
    }
}