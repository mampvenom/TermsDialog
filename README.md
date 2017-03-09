# TermsDialog

[![](https://jitpack.io/v/mampvenom/TermsDialog.svg)](https://jitpack.io/#mampvenom/TermsDialog)

Setup
====================

Step 1. Add the JitPack repository to your build file.

Add it in your root build.gradle at the end of repositories:

```Groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

Step 2. Add the dependency

```Groovy
dependencies {
	        compile 'com.github.mampvenom:TermsDialog:1.0.0'
}
```

How to use
====================

```Java
new MampTermsDialogFragment.Builder()
                        .create().show(getSupportFragmentManager());
```
