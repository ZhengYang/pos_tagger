java build_tagger bin-0.train sents.devt bin-0.model
java run_tagger bin-0.test bin-0.model bin-0.out
java diff bin-0.ans bin-0.out bin-0.model

java build_tagger bin-1.train sents.devt bin-1.model
java run_tagger bin-1.test bin-1.model bin-1.out
java diff bin-1.ans bin-1.out bin-1.model

java build_tagger bin-2.train sents.devt bin-2.model
java run_tagger bin-2.test bin-2.model bin-2.out
java diff bin-2.ans bin-2.out bin-2.model

java build_tagger bin-3.train sents.devt bin-3.model
java run_tagger bin-3.test bin-3.model bin-3.out
java diff bin-3.ans bin-3.out bin-3.model

java build_tagger bin-4.train sents.devt bin-4.model
java run_tagger bin-4.test bin-4.model bin-4.out
java diff bin-4.ans bin-4.out bin-4.model

java build_tagger bin-5.train sents.devt bin-5.model
java run_tagger bin-5.test bin-5.model bin-5.out
java diff bin-5.ans bin-5.out bin-5.model

java build_tagger bin-6.train sents.devt bin-6.model
java run_tagger bin-6.test bin-6.model bin-6.out
java diff bin-6.ans bin-6.out bin-6.model

java build_tagger bin-7.train sents.devt bin-7.model
java run_tagger bin-7.test bin-7.model bin-7.out
java diff bin-7.ans bin-7.out bin-7.model

java build_tagger bin-8.train sents.devt bin-8.model
java run_tagger bin-8.test bin-8.model bin-8.out
java diff bin-8.ans bin-8.out bin-8.model

java build_tagger bin-9.train sents.devt bin-9.model
java run_tagger bin-9.test bin-9.model bin-9.out
java diff bin-9.ans bin-9.out bin-9.model