FROM gradle:7.2.0-jdk8

COPY . /home/reloc-evaluator

WORKDIR /home/reloc-evaluator

RUN mkdir app/log && mkdir app/cache && mkdir app/result
RUN gradle clean build

CMD ["gradle", "run"]