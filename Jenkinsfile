pipeline {
  agent any
  stages {
    stage('Wait') {
        steps {
            sleep 30
        }
    }
    stage('Checkout Deps') {
      steps {
        checkout([$class: 'GitSCM', branches: [[name: env.BRANCH_NAME]], extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: '../API']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/RockBottomGame/API']]])
        checkout([$class: 'GitSCM', branches: [[name: env.BRANCH_NAME]], extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: '../Assets']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/RockBottomGame/Assets']]])
      }
    }
    stage('Clean') {
      steps {
        sh './gradlew clean --no-daemon'
      }
    }

    stage('Build') {
      steps {
        sh './gradlew build --no-daemon'
      }
    }

    stage('Upload Artifacts') {
      steps {
        archiveArtifacts 'build/libs/**.jar'
      }
    }

    stage('Publish') {
      when {
        branch 'main'
      }
      steps {
        sh './gradlew publish --no-daemon'
      }
    }

  }
  environment {
    local_maven = '/var/www/maven'
  }
}
