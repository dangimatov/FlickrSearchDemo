##  Flickr Search Api Client Demo

### Preview

![Preview](screenshot.png)

### Overview
This project is an Android demo client for [Flickr's Search Api ](https://www.flickr.com/services/api/flickr.photos.search.html). It allows to search for images based on given search query. It was intentionally build using no 3rd party libraries as Coding Challenge for Uber interview.



### Architecture
App utilises Clean architecture and uses feature-based package structure.
Based on this almost all of the code (except DI) can be found in search directory since there is only 1 feature in this app. Inside it is split into 3 layers - model, domain and view


*  **Model** -  Responsible for providing required data to business logic. To communicate with this layer there are Repository interfaces which are implemented by SearchApiClient and ImageLoader.

*  **Domain** -  Contains most of a business logic. Includes 1 lifecycle-aware Interactor which is responsible for state of the view at any given moment. This logic is completely AndroidFramework-free and is fully tested with Junit.

*  **View** -  Includes code which directly affects ui. Consists of Interfaces to talk with other layers, Android ui components and set of State classes each of which represents 1 state of the view and then is mapped by the view to some views properties changes. It's also worth mentioning that ui state changes can only happen by calling 1 method `void updateState(State state)` which greatly improve debugging and makes app less error prone.

### Additional Notes
* Android implementation of LRUCache was used to cache most recently used images. Size was chosen to fit ~50-60 images which is 2-3 'screens' of fresh images on a regular sized phone.
* Most of the communication between layers is done using Observer pattern which helped with making logic lifecycle-aware and prevent possible leaks
* Repositories have 'soft' cancelation logic. If task is already executing it will not interrupt the thread but if there are still unexecuted tasks in a queue it will clean it.


### Things to improve given more time
* Better UX
* Use less mutable states. Possibly try to use Redux since some of the states depend on previous ones
* Try to generalise Observer and maybe implement at least basic Rx interfaces