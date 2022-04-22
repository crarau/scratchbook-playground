In this playground I will be simulating this scenario:
* listening to an external stream of data
* processing the data to extract the relevant information
* backing up the data to an external backup service in BigQuery
* calling an external API to further process the data, 
  * note: but not more than 10 requests at a time to limit the load on the external API
* calling the external API to notify that the data has been processed

In this exercise we want to make sure that the threads are being used correctly
we want to fully test the data processing pipeline