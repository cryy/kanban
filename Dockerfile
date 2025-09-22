FROM ubuntu:latest
LABEL authors="gab"

ENTRYPOINT ["top", "-b"]