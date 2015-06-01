# MaJorToM #

The MaJorToM ( Merging Topic Maps Engine ) project was founded to develop a lightweight, merging and flexible Topic Maps engine satisfying different business use cases. The engine provides a couple of new features above to other engines based on the Topic Maps API version 2.0.

MaJorToM is more than just a Topic Maps engine, it's a kind of philosophy of designing an independent Topic Maps engine. The MaJorToM API extends the Topic Maps API 2.0 to provide a simple way for accessing the advanced features of next generation engines. Because of this downward compatibility, the engine can be used as drop-in replacement for any other Topic Maps engine based on the TMAPI without any modifications. The architecture of MaJorToM is designed for satisfying the goals of simplicity and flexibility and splits the information and application domain from each other. The application only uses a set of object stubs to communiate with the underlying store. The objects do not contain any knowledge about the subjects they represent. The knowledge is encapsulated by the underlying store, which offers a set of abstract methods to access this information. The store may not be a real topic map store which means it doesn't have to use the Topic Maps paradigm for data storage.

The engine comes with a huge set of advanced features:

## modeling of time and space ##

The engine supports additional occurrence types for time and space. Using this information a user can create statements about geographical locations or real time events. As special feature the information can be used to query for example all subjects born in a specific time range or located near a specific location.

## complete chain of evidence ##

The engines store all changes happening during the runtime in the context of a specific subject. The information can be used to explore the timeline back the creation of any subject. The history information is never lost.

## monitoring changes ##

The engine provides an event model which an application can use to watch changes of a specific subject or context. The underlying engine notifies all listeners if an event happens. A topic map can be reprensented as streams of structured facts which is more dynamic and lightweight than the normal full TMAPI view of topic maps.

## transaction management ##

MaJorToM provides a transaction model operating on the in-memory store. Every transaction creates a new view on the topic map which is independent from any other transaction. Because of that the transaction can be used for doing a couple of modifications as one atomic change.


The community is what makes the flourish of such projects. Feel free to download and use it and become a community member by reporting bugs, making features suggestions or even contributing code to the MaJorToM project. Your ideas for improvement and new features are welcome.

## API documentation ##

See the [JavaDocs](http://docs.topicmapslab.de/majortom/javadoc/index.html) for more information about the MaJorToM API.