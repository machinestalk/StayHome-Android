#!/usr/bin/env groovy 
node('master')
{
        env.ANDROID_ZIPALIGN ="/android-sdk-linux/build-tools/27.0.3/zipalign"
        def andgradle
  stage('checkout') {
        checkout scm
    }
  stage('build & sign APK'){
      //    andgradle = docker.build("andgradle", ".")
        env.ANDROID_ZIPALIGN ="/android-sdk-linux/build-tools/27.0.3/zipalign"
        andgradle = docker.image("andgradle")
        andgradle.inside('-v ${PWD}:/app -w /app'){
            sh 'mv /local.properties .'
            sh './gradlew clean assembleRelease --stacktrace'
            sh ' ${ANDROID_ZIPALIGN} -f -v 1 app/build/outputs/apk/mock/release/MT_Dev.apk app/build/outputs/apk/mock/release/MT_Devv.apk '
        //           step([$class: 'SignApksBuilder', apksToSign: 'app/build/outputs/apk/mock/release/MT_Dev.apk', keyAlias: 'CarsTalkAlias', keyStoreId: 'carstalk'])
        signAndroidApks ( 
        keyStoreId: "carstalk",
        keyAlias: "CarsTalkAlias",
        apksToSign: "app/build/outputs/apk/mock/release/MT_Devv.apk",
        skipZipalign: true,
        // uncomment the following line to output the signed APK to a separate directory as described above
        // signedApkMapping: [ $class: UnsignedApkBuilderDirMapping ]
        // uncomment the following line to output the signed APK as a sibling of the unsigned APK, as described above, or just omit signedApkMapping
        // you can override these within the script if necessary
        
    )
   }
    }
     /* stage('SonarQube'){
         andgradle = docker.build("andgradle", ".")
         andgradle = docker.image("andgradle")
         andgradle.inside('-v ${PWD}:/app -w /app'){
              sh 'mv /local.properties .'
              sh './gradlew sonarqube'
                }
    }*/
    stage('Uplod .apk to fabric'){

          fabric apiKey: '130091aac3069ac01b49440b6687fa36a545ef4e', 
          apkPath: 'app/build/outputs/apk/mock/release/MT_Devv-signed.apk', 
          buildSecret: '835b1859b8614f5d2ced2153435f88db92456fbdbb0272f28b4bd4e43a78f465',
          notifyTestersType: 'NOTIFY_TESTERS_EMAILS', organization: 'Machinestalk', 
          releaseNotesFile: 'app/release_notes.txt', releaseNotesParameter: 'FABRIC_RELEASE_NOTES',
          releaseNotesType: 'RELEASE_NOTES_FILE',
          testersEmails: 'nezih.dammak@machinestalk.com,machinestalktunisia@gmail.com,chaker.fellari@Machinestalk.com,ahmed.elyakoubi@machinestalk.com,maher.benrhouma@machinestalk.com,rachdi.aymen@machinestalk.com,afef.zenned@machinestalk.com,nabil.khedhri@Machinestalk.com,yahya.ibrahmi@machinestalk.com,	oussama.mensi@machinestalk.com, mortadhaguedhami@gmail.com, mactalk.developers@gmail.com, Fahad.Suleman@Machinestalk.com, shan.ahmed@Machinestalk.com, maryam.hassan@machinestalk.com, mohammed.alqushah@machinestalk.com, sanaa.alsarkhi@machinestalk.com, syedvaqas.masood@machinestalk.com, slim.ouertani@machinestalk.com',
          testersGroup: '', useAntStyleInclude: false
    }


}
