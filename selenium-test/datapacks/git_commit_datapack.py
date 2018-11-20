import subprocess
import os
import pygit2
import shutil


class AddToGitFile:

    def __init__(self, relative_path):
        self.relative_path = relative_path

    def setup_file(self, local_path):
        raise NotImplementedError


class AddToGitConcreteFile(AddToGitFile):

    def __init__(self, relative_path, content):
        super(AddToGitConcreteFile, self).__init__(relative_path)
        self.content = content

    def setup_file(self, local_path):
        file_path = str(local_path / self.relative_path)
        os.makedirs(os.path.dirname(file_path), exist_ok=True)
        with open(file_path, 'w') as f:
            f.write(self.content)


class AddToGitSymlinkFile(AddToGitFile):

    def __init__(self, relative_path, source_path):
        super(AddToGitSymlinkFile, self).__init__(relative_path)
        self.source_path = source_path

    def setup_file(self, local_path):
        file_path = str(local_path / self.relative_path)
        os.makedirs(os.path.dirname(file_path), exist_ok=True)
        shutil.copyfile(self.source_path, file_path)


class GitCommitDatapack:

    def __init__(self, commit_message, username, email):
        self.add_to_git_files = []
        self.commit_message = commit_message
        self.username = username
        self.email = email

    def add_file(self, file):
        self.add_to_git_files.append(file)

    # TODO:
    # Support customized branch name.
    def commit_to_repo(self, local_path):
        for add_to_git_file in self.add_to_git_files:
            add_to_git_file.setup_file(local_path)

        repo = pygit2.Repository(str(local_path))
        # reference = "refs/heads/master"
        index = repo.index
        index.add_all()
        index.write()
        author = pygit2.Signature(self.username, self.email)
        commiter = pygit2.Signature(self.username, self.email)
        message = self.commit_message
        tree = repo.index.write_tree()
        if repo.head_is_unborn:
            parent = []
        else:
            parent = [repo.head.get_object().hex]
        repo.create_commit("refs/heads/master", author, commiter, message, tree, parent)

        # pygit2/libgit2 doesn't support hooks (which get bypassed silently).
        # https://github.com/libgit2/libgit2/issues/964
        # https://github.com/libgit2/libgit2/pull/3824
        # repo.remotes["origin"].push(["refs/heads/master"])
        #
        # TODO:
        # Consider to replace pygit2 with the following libs, to see if they have
        # better support.
        # https://github.com/gitpython-developers/GitPython
        # https://github.com/dulwich/dulwich
        push_process = subprocess.Popen(
            ["git", "push", "origin", "master"],
            cwd=str(local_path),
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE)
        output, errors = push_process.communicate()
        print("git push output:\n{}".format(output.decode('UTF-8')))
        print("git push errors:\n{}".format(errors.decode('UTF-8')))
