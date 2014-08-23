IntranetSearchEngine-windows
============================

My Major Project of B. Tech. at National Institute of Technology Uttarakhand.

This Intranet Search Engine is a text document search engine within a network. It can be used to find most suitable documents stored on the public servers in network. The search engine gives the recommendation of documents by ranking them according to the user information need.

There may be thousands of E-books and text documents stored in a network. Generally, users do not know which documents are most suitable for their need and where it is stored in the network. The search engine helps to quickly find the relevant documents and rank-wise shows them as per their relevancy.

For example a user want to find documents on “Electric Motors” traditionally, the user has to access to ftp server and then look at all the directories and files one by one by their names, then download any of them. But, the user may download any non-relevant document which has just name related to the user need, or there may be plenty of other documents on the ftp server which are more suitable for “Electric Motors”. In another example the user can just type “Electric Motors” in the query search field of the search engine and within a moment the search engine will respond with the list of documents suitable according to the query.

The project consist of two software layers - client side application and server side software. The client side application in a GUI based app which allows user to type query and then shows results according to the query. Server side software layer consist of two applications (two processes), Indexer and KHOJI-server. Indexer does crawling and indexing of the data available at ftp servers. KHOJI-server listen for client requests and responds the user queries by retrieving the results.
The complete project is implemented in JAVA. The project uses Information Retrieval, Socket Programming, Multithreading, GUI, Client-Server architecture, and Model-View-Controller (MCV) software pattern.
