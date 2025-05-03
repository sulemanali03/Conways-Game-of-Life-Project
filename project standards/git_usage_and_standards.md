## Git / GitHub Standards
> **Purpose:** This is not intended to be a git tutorial, but rather provide a normalized standard for file sharing and version control. Also this document is just a starting point to keep us on the same page, but the actual practices can be negotiated if they are unwieldly or incomplete. It is just helpful if we try to comply by the same version control standards to prevent undesirable surprises. It is fine to change something about the standard provided that everyone knows about it and consents to it. 

> ### Basic git commands
> > *stage git changes:* `git add <file1> <file2> ... ` or `git add .` for all changes  
> > *locally commit staged changes:* `git commit -m "message"`  
> > *push the changes to the remote GH repo:* `git push origin <branch_name>`
> > *pulling changes from the remote GH repo:* `git pull origin <branch_name>` *please note that this will pull and attempt to merge the remote code with yours. Commit your changes locally before doing this.*  
> > *fetching code from remote without merging:* `git fetch origin <branch_name>`  
> > *switch to another branch:* `git checkout <branch_name>`  
> > *Check up on your local git repo:* `git status`  
> > *Check for differences between another branch:* `git diff <branch_name>`   
> > *Look at past commits so that you can revert to them if needed:* `git log`  
> > *See all other git branches:* `git branch -a`  

> ### Branches
> > **Purpose:** Git branching is super important for allowing many people to work on the same project without breaking things for everyone else. These are the branching standards that we will use throughout this project.  
> > **Notes about main:** Branches are super useful especially for ongoing or incomplete work. We will use the **main** branch as the production branch where features and attributes of the project will live once completed. The main branch should ALWAYS be stable, anyone should be able to clone the main branch and easily follow simple steps to get it running.   
> > **Branching practices:** A new branch can be created using the `git branch <branch_name>`. This will also essentially copy all the code and history from your current branch to the new one. If you are going to make a fairly major update to a current aspect of the project or add a new feature to the project it works well to make a new branch named feature_name_wip (wip stands for work in progress). Once the feature has reached a stable mile stone or is complete it can be merged back into the main branch.    
> > **Working with existing feature branches:** If a feature branch already exists for something that you are working on, you can make a work in progress branch of that feature and then merge it in once you are ready.  

> ### Branch Naming Conventions
> > **Purpose:** Using consistent and descriptive naming conventions for branches helps in identifying their purpose and status.  
> > **Common Conventions:**  
> > 1. **Feature Branches:** `feature/<description>` (e.g., `feature/login-page`) *An new component or part of the code. Features should be stable*  
> > 2. **Bugfix Branches:** `bugfix/<description>` (e.g., `bugfix/fix-login-error`) *Patch to any fairly major flaw in the code or documents in the project. Minor bugs can just be fixed and commited directly with a descriptive name.*  
> > 3. **Documentation Branches:** `docs/<description>` (e.g., `docs/project_description`) *Since much of this project is about software engineering we will be working a lot with documents and planning. The docs branch directory will be used to add to and modify any of our documentation*  
> > 1. **Work in Progress (WIP) Branches:** `wip/<description>` (e.g., `wip/new-feature`) *Used to add / work on new components in the project. Works in progress are assumed to be unstable. Once merged back into the desired branch the wip branch can be deleted using `git branch -d wip/feature_wip` && `git push origin --delete wip/feature_wip`*  

> ### Merging and conflict resolution
> > **Purpose:** This is probably where the most problems are encountered when using git. Using good management of branches along with frequent commits helps in mitigating against nasty merge conflicts that need to be manually resolved.  
> > **Merging feature branch into another branch:** Once you have a feature that is stable or complete you may need to merge it into another branch.  
> > 1. stage and commit all local changes. `git add . ; git commit -m "message"`  
> > 2. checkout the branch that you want to merge the feature into. `git checkout <base_branch>`  
> > 3. merge the feature branch into the base branch `git merge <branch_name>` *There are plenty of merge strategies that can be applied through git for various purposes, but they can be found pretty easily if needed.*  
> > 4. resolve merge conflicts if there are any. *This is usally best done through your ide. If using vscode you can install an git extension and an interface for resolving conflics should come up if there are any issues.*  
> > 5. stage and commit the merge resolutions.  

> ### Best practices
> > 1. Commit frequently. After you make a change commit that.  
> > 2. Always commit you changes after a working on the project.  
> > 3. Please don't commit broken code. Wait until a feature is complete.  
> > 4. 

> ### Git resources that I like
> > **More notes on best practices for git:** https://gist.github.com/luismts/495d982e8c5b1a0ced4a57cf3d93cf60