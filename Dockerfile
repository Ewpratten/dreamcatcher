FROM gradle:jdk11

ARG VERSION="0.10.11"

RUN apt update -y
RUN apt install gdebi -y
RUN wget https://github.com/caddyserver/caddy/releases/download/v2.3.0/caddy_2.3.0_linux_amd64.deb -O /caddy.deb
RUN gdebi -n /caddy.deb 

COPY . /work

EXPOSE 80 

ENTRYPOINT ["/work/run.sh"]