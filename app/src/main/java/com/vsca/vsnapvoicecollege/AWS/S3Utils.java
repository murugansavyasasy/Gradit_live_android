package com.vsca.vsnapvoicecollege.AWS;

import android.content.Context;
import android.util.Log;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class S3Utils {



   public static String generates3ShareUrl(Context applicationContext, String collegeid, String path, String fileNameDateTime) {
      File f = new File(path);
      AmazonS3 s3client = AmazonUtil.getS3Client(applicationContext);
      ResponseHeaderOverrides overrideHeader = new ResponseHeaderOverrides();
      String mediaUrl = f.getName();
      String  filedate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
      File Datefolder=new File(filedate);
      Log.d("datefilenew", String.valueOf(Datefolder));
      GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(AWSKeys.BUCKET_NAME, collegeid+"/"+Datefolder+"/"+fileNameDateTime+"_"+mediaUrl);
      generatePresignedUrlRequest.setMethod(HttpMethod.GET);
      generatePresignedUrlRequest.setResponseHeaders(overrideHeader);
      URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
      String fileUrl = url.toString().substring(0, url.toString().indexOf('?'));
      Log.e("s", fileUrl);
      return fileUrl;
   }
}



