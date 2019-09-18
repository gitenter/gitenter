from git import Repo
import os
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

    def __init__(self, commit_message):
        self.add_to_git_files = []
        self.commit_message = commit_message

    def add_file(self, file):
        self.add_to_git_files.append(file)

    # TODO:
    # Support customized branch name.
    def commit_to_repo(self, local_path):
        for add_to_git_file in self.add_to_git_files:
            add_to_git_file.setup_file(local_path)

        repo = Repo(str(local_path))
        with repo.config_writer():
            repo.git.add("-A")
            repo.index.commit(self.commit_message)

        origin = repo.remotes['origin']
        origin.push("master")
