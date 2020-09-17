pipeline {
  agent any
  stages {
    stage('Checkout Deps') {
      steps {
        checkout([$class: 'GitSCM', branches: [[name: 'env.BRANCH_NAME']], extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: '..']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/RockBottomGame/API']]])
        checkout([$class: 'GitSCM', branches: [[name: 'env.BRANCH_NAME']], extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: '..']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/RockBottomGame/Assets']]])
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
        branch 'master'
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
