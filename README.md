# not-reddit
[![Build Status](https://travis-ci.org/scb5304/not-reddit.svg?branch=master)](https://travis-ci.org/scb5304/not-reddit)

<img src="https://i.imgur.com/OqVKfxS.jpg" width="35%"/> <img src="https://i.imgur.com/cC8vNh3.jpg" width="35%" />

**NotReddit**, as in "I have not read it" is an Android app for browsing Reddit. 

The app hides posts once the user has scrolled past them to ensure users don't see posts they've seen before. The comments section for each post is given prime screen real estate, requiring only a single tap to be taken there. Viewing your subscribed subreddits is also available via a Floating Action Button at the bottom of the screen, which makes it easy on larger phones to switch between communities. The aim is to focus on the lurkers of Reddit that want to view new content in an intuitive way.

## Project Status

I developed this app outside of work to learn about the new MVVM architecture components released by Google. My previous apps have been MVP, so this project is a relatively simple use-case to figure out DataBinding, ViewModels, LiveData, etc. I also got to implement newer Dagger practices on Android, cement my knowledge of user accounts in Android (AccountManager/ OAuth2 / OkHttp Interceptors), and have a lot of fun building a Material Design UI.

The app is not finished in any capacity that I'd be proud to release it to an app store, but it does work quite well. There's missing functionality and features I would want implemented prior to distributing it to users, plus a pretty big gap in unit testing. I plan to shelve this for a while since I've learned what I've wanted to, and I'd rather learn some new technologies than continue doing what I do at work, outside of work.

## Architecture

The application is essentially a master-detail interface, where the user is displayed a list of posts (a subreddit) and the contents of that post are shown by tapping it. Since this is Reddit, a post can either be content from an external source, or simply "selftext" displayed within Reddit. For example, it could be a picture of a cute cat on Imgur, or it could be some text complaining about how Akali is OP in League of Legends. Chrome Custom Tabs are used to display web pages within the NotReddit's context, so that's where you'd find your cute cats. In contrast, a PostDetailFragment would be attached to view the Janna post.

NotReddit is one Activity: the MainActivity, which houses one of two Fragments: PostListFragment and PostDetailFragment. Each fragment observes data housed inside of its ViewModel. These ViewModels use an RxJava2 Repository layer to retrieve data from the Reddit API in a clean fashion, then post the retrieved data to the LiveData the Fragment is observing. 

For example, in PostList, a reference is held to the current subreddit. When first visiting the subreddit or refreshing the screen, the ViewModel retrieves a bunch of posts from the Reddit API. When the user scrolls near the bottom of the content, the ViewModel is informed and in turn asks Reddit for the posts which come after the current listing. The ViewModel appends these new posts to the current model, telling the RecyclerView precisely how many posts have been added.

## Authentication

The OAuth2 "code flow" is used to acquire tokens.   
https://github.com/reddit-archive/reddit/wiki/OAuth2#token-retrieval-code-flow. 

Here's the order of actions in a typical login flow:

1) User taps login in MainActivity's toolbar menu.
2) MainActivity tells the Accountant singleton to log in.
3) Accountant checks how many accounts NotReddit has. 
	If there are already accounts, we let them choose from an existing one or create a new one using AccountManager#newChooseAccountIntent. 
	If there are no accounts, immediately open the AuthActivity.
4) AuthActivity displays a WebView with the Reddit login page.
5) AuthActivity intercepts the Redirect URI from the WebView, containing the code to log in, passing it to Accountant.
6) Accountant asks the FullTokenRepository to get a token using this code (a token which also has the user name, which we don't have).
7) Accountant adds the account or updates the account if it already exists. The current logged in username is put in SharedPreferences.
8) Accountant uses the passed Activity reference (an AuthActivity) and tells it we've been logged in.
9) AuthActivity dismisses itself, displays a toast, and shows MainActivity.

NotReddit uses the AccountManager API to hold reference to accounts logged into through this app's OAuth login flow. No password information is read or persisted: only the access token and refresh token. An OkHttp3 Interceptor appends the current access token to network request made to Reddit's OAuth API. It also attempts to retrieve a new one using our refresh token if we receive a 401.

## Dependencies

* [AppCompat/Design/CardView/GridLayout](https://developer.android.com/topic/libraries/support-library/) support libraries for Material theming and layouts.
* [Timber](https://github.com/JakeWharton/timber), for logging.
* [Stetho](http://facebook.github.io/stetho/), for debugging.
* [OkHttp](https://github.com/square/okhttp), for networking.
* [Glide](https://github.com/bumptech/glide), for image fetching/display.
* [HTMLTextView](https://github.com/PrivacyApps/html-textview), for displaying post / comment content.
* [Android CustomTabs](https://github.com/saschpe/android-customtabs), helper library that wraps the implementation of CCT.
* [ViewModel/LiveData](https://developer.android.com/topic/libraries/architecture/adding-components) because architecture components.
* [Retrofit](http://square.github.io/retrofit/), for RESTful communication on top of OkHttp.
* [RxJava2](https://github.com/ReactiveX/RxJava), for observing network operations.
* [Dagger](https://github.com/google/dagger), for dependency injection.
* [Guava](https://github.com/google/guava), for helper methods, and the Range data structure. Potentially overkill.
* [threetenabp](https://github.com/JakeWharton/ThreeTenABP), Jake Wharton's JSR-310 (Java8 date/time) backport for Android.
* [Mockito](http://site.mockito.org/), for unit testing.
