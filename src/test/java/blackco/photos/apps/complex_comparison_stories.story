Narrative: 
This app compares photos on your hard drive to those stored on Flickr.
It will scan nominated directories on your hard drive.

The app has the following features, tested here
1) Retrieving a set of photos from flickr by time taken will always return photos taken within the same second.
2) Compares photos by date and time ( to second ) & camera used to determine uniqueness.
3) Since it takes time to query flickr ( photo by photo ), the app remembers which photos on your hard drive it has processed, even if it cannot find a match on flickr


!-- retrieving photos by the second
Given the photos stored on Flickr:
|id|dateTaken|camera|
|1|2015-06-02 20:01:01|iPHONE|
|2|2015-06-02 20:01:02|iPHONE|
|3|2015-06-02 20:01:03|Test Camera|
When retrieving photos taken at 2015-06-02 20:01:02
Then returned photo set has 1 photos

!-- retrieving photos by the second
Given the photos stored on Flickr:
|id|dateTaken|camera|
|1|2015-06-02 20:01:01|iPHONE|
|2|2015-06-02 20:01:02|iPHONE|
|3|2015-06-02 20:01:03|Test Camera|
When retrieving photos taken at 2015-06-01 07:00:00
Then returned photo set has 0 photos


!-- comparing two like photos
Given two photos:
|id|dateTaken|camera|StoredOn
|4|2015-06-02 20:01:01|iPHONE|Flickr|
|5|2015-06-02 20:01:01|iPHONE|My Computer|
When photos stored on my computer and flickr are compared
Then both photos are the same


!-- comparing two unlike photos
Given two photos:
|id|dateTaken|camera|StoredOn
|4|2015-06-02 20:01:01|iPHONE|Flickr|
|5|2015-06-02 20:01:01|iPHONE6|My Computer|
When photos stored on my computer and flickr are compared
Then both photos are the different

Given two photos:
|id|dateTaken|camera|StoredOn
|4|2015-06-02 20:01:01|iPHONE|Flickr|
|5|2015-06-02 20:01:02|iPHONE|My Computer|
When photos stored on my computer and flickr are compared
Then both photos are the different

Given two photos:
|id|dateTaken|camera|StoredOn
|4|2015-06-02 20:01:01|iPad|Flickr|
|5|2015-06-02 20:01:02|iPHONE|My Computer|
When photos stored on my computer and flickr are compared
Then both photos are the different


!-- processing photos, tracking what is matched,unmatched and what is not processed
Given two photos:
|id|dateTaken|camera|StoredOn
|4|2015-06-02 20:01:02|Canon|Flickr|
|5|2015-06-02 20:01:02|Canon|My Computer|
When this set of photos is processed
Then there are no unmatched photos
And there are no unprocessed photos
And the two photos match

Given a set of processed photos
When this set of photos is processed
Then these photos are not reprocessed
