**jSmugmugBackup** is a litte **commandline** tool i'm creating to backup and restore my pictures and videos on http://www.smugmug.com

I created this project mainly for my own needs. Besides sharing my photos,
I use [SmugMug](http://www.smugmug.com) as a backup storage for my photos and videos. jSmugmugBackup does not modify local files (at least thats not the intention), nor does it allow you to delete¹ content on your account, so all you can do wrong, hopefully, does not destroy any data ... but maybe you'll end up having too much of it :-)
The program has a commandline interface which should be working fine. Try the **"--help"** parameter to get you started. There's also a GUI (deactivated in the release version), but it's still buggy and doesn't do much yet.

As to the state of this software, i refuse to call it "alpha" or "beta" or
anything else. It works for me in the way i want it to work. If you want
to know exactly what the program does, you should download, read and compile the source
code. I take no responsibility for whatever this program will do to you, your
[SmugMug](http://www.smugmug.com) account, your computer or anyone/anything else.

I would like to thank especially Anton Spaans AKA Flying Dutchie ([SmugFig API](http://blog.antonspaans.com/smugfig-api)) and Kallasoft ([SmugMug Java API](http://www.kallasoft.com/smugmug-java-api/)) for the inspiration (early versions of jSmugmugBackup were using their API's) and of course the guys from [SmugMug](http://www.smugmug.com) for providing such an awesome photo service.

If you find any bugs, need help or have anything else on your mind .. drop me a message in the discussion group and I'll see what i can do about it. Of course everyone is very welcome to participate in this project!

features:
  * **uploading** and **downloading** pictures and videos to [SmugMug](http://www.smugmug.com) (the directory structure is mapped as far as possible to categories, subcategories and albums on your [SmugMug](http://www.smugmug.com) account, i.e. one-way sync)
  * **listing** the contents of your [SmugMug](http://www.smugmug.com) account
  * **sorting** categories, subcategories and albums on [SmugMug](http://www.smugmug.com) alphabetically ([SmugMug](http://www.smugmug.com) sorts them by upload time)
  * **verifying** / comparing your local pictures and videos with items stored on your [SmugMug](http://www.smugmug.com) account (checking filenames, sizes and optionally MD5 sums)
  * automatically **tagging** pictures and videos based on their album name
  * **anonymous login**, so you can download your friends public galleries
  * downloading **album URLs** (password protected albums are not yet supported, sorry)

requirements:
  * **Linux**, **Mac** and **Windows** (not really tested) should be fine
  * **Java** 1.6, since my Mac has only Java 1.5 there is a little hack to make it work in 1.5 too
  * lots of RAM if you want to upload big videos, 1GB would be nice, but less is probably fine too

bugs:
  * none :-) ... well, that would be nice ...
  * the GUI is still very buggy, so i disabled it for the current release. If you want to try it out, download and compile the source code.
  * the thing to which i don't really have a solution right now, is that under some rare circumstances, which i don't know yet, some uploaded galleries in a category aren't shown on the [SmugMug](http://www.smugmug.com) website in the "categories" display mode. The Galleries are there, can be downloaded and can also be seen when selecting "display: galleries". Using the sort-function of jSmugmugBackup several times, seems to solve the problem. If you experience this too, please drop me a message in the forum.
  * some other limitations are described under "notes" when you start jSmugmugBackup with the "--help" parameter


---

¹ - well, there is a delete function implemented internally, but it's only used for some dirty hack in the sorting function and shouldn't do any harm.