LATEX?=xelatex

.PHONY: default pdf distclean clean

default: pdf

hardwareAPI.o: hardwareAPI.c
	gcc -c $^

test-hwAPI: test-hwAPI.c hardwareAPI.o
	gcc -o $@ $^ -lpthread -lnsl

controller: controller.c hardwareAPI.o
	gcc -o $@ $^ -g -ansi -Wall -O3 -lpthread -lnsl

pdf: report.pdf

report.pdf: report.tex
	$(LATEX) report
	$(LATEX) report

distclean: clean
	rm -f controller report.pdf test-hwAPI

clean:
	rm -f *.aux *.log *.o *.out
