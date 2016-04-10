# photos
reconcile your flickr account to your hard drive

Dependencies  
~~~~~~~~~~~~  
1) java 1.7.0_55 or greater  
2) maven  
~~~    

  
  
Run  
~~~
1) git clone https://github.com/blackco/photos.git  
2) cd photos  
3) mvn deploy  
4) java blackco.photos.apps.ComplexComparison  
-userId flickrUserId  
-reqKey flickr Api key 
-reqSecret flickr api key 
-accessKey flickr api key 
-accessSecret flickr api key 
-path <path of photos you wish to reconcile>
-cache <local file on your hard drive to store results> 
-tryAgain <true | false> selecting true will call flickr again to attempt to reconcile any unmatched photos
~~~
