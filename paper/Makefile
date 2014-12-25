BASENAME=paper

.PHONY: default
default: clean compile
	
compile:
	pdflatex $(BASENAME)
	bibtex $(BASENAME)
	pdflatex $(BASENAME)
	pdflatex $(BASENAME)

.PHONY: clean
clean:
	rm -f *.aux *.bbl *.bla *.blg *.dvi *.loa *.lof *.log *.lot *.out *.tdo *.toc *-blx.bib *run.xml *.cut *.gxg *.glx *.ilg *.lol *.gxs *.acn *.glo *.ist *.acr *.alg *.glg *.gls *.idx *.ind *.xdy
	rm -f *.ps *.pdf
	rm -f $(BASENAME).zip
