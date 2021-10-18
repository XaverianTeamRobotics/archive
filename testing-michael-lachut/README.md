...
yeet this only works in the _references dir with a permalink why is jekyll this way i do not know

so should i do everything in _/references/testing-Michael-Lachut.md ?

yee

alright so what should i do if i need to use a folder

add it to the includes list in _config.yml, like _references/folderName and then add a permalink to the top of the file like
---
permalink: the link relative to root
---
