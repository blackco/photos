
CREATE DATABASE flickr;

use flickr;


CREATE USER 'photos'@'localhost' IDENTIFIED BY 'elephants';
GRANT INSERT ON flickr.* TO 'photos'@'localhost';
    GRANT EXECUTE ON PROCEDURE flickr.updatePhotos TO 'photos'@'localhost';
GRANT UPDATE ON flickr.* TO 'photos'@'localhost';

CREATE TABLE photos (
        flickrId varchar(30)
    ,   downloaded boolean
    ,   url varchar(255)
    ,   title varchar(255)
    ,   takenInUtc timestamp
    ,   camera varchar(255)
);


DELIMITER //

CREATE PROCEDURE updatePhotos(IN _flickrId VARCHAR(255), \
                        IN _downloaded BOOLEAN, \
                        IN _url VARCHAR(255), \
                        IN _title VARCHAR(255), \
                        IN _takenInUtc timestamp, \
                        IN _camera VARCHAR(255))
BEGIN



        UPDATE photos
        SET downloaded = _downloaded
        ,   url = _url
        ,   title = _title
        ,   takenInUtc = _takenInUtc
        ,   camera = _camera
        WHERE  flickrId = _flickrId;

        DROP TABLE IF EXISTS f;

        DROP TABLE IF EXISTS g;


        CREATE TEMPORARY TABLE f ( flickrId VARCHAR(255)
                            ,   downloaded BOOLEAN
                            ,   url VARCHAR(255)
                            ,   title VARCHAR(255)
                            ,   takenInUtc  timestamp
                            ,   camera varchar(255));

        CREATE TEMPORARY TABLE g ( flickrId VARCHAR(255));


        INSERT f VALUES ( _flickrId, _downloaded, _url , _title, _takenInUtc, _camera);

        INSERT g SELECT flickrId from photos;


        INSERT photos
        SELECT f.*
        FROM f LEFT JOIN g ON f.flickrId = g.flickrId
        WHERE g.flickrId is NULL;


        DROP TEMPORARY TABLE f;

        DROP TEMPORARY TABLE g;

END
//

DELIMITER;

insert photos values ("1",false,"https://farm4.staticflickr.com/3894/14843958999_a20a638c64_o.jpg", "IMG_0201", "2005-04-01 12:14:01","Canon X");

CALL updatePhotos("1",false,"http://blah.com",null,null,"IPHONE");

CALL updatePhotos("3",false,"http://blah.com","IMG_0205",null,null);

CALL updatePhotos("3",false,"http://blah.com","IMG_0205","2005-04-01 12:14:01",null);

CALL updatePhotos("3",false,"http://blah.com","IMG_0205","2005-04-01 12:14:01","iPhone 5");



DELIMITER //

 CREATE PROCEDURE GetAllPhotos()
   BEGIN
        SELECT *  FROM photos;

       UPDATE photos
       SET downloaded = false
       WHERE flickrId = 1;


    INSERT photos
    SELECT 2
    ,      false
    ,      "http://blah.com"
    ,      "IMG_0202"
    ,      "2005-04-15 15:10:07"
    ,      "Canon X"
    FROM photos WHERE NOT EXISTS ( SELECT * FROM photos WHERE flickrId = 2 );

   END //
 DELIMITER ;

