#!/usr/bin/python2.4


from elementtree.ElementTree import ElementTree
from datetime import datetime
from time import strptime


def main(argv):

    tree = ElementTree (file=argv[1])
    items = tree.getroot().find("channel").findall("item")

    try:
        number = int (argv[2])
    except:
        number = len (items)


    print '<html><head><style type="text/css" media="all"> @import "tc18.css";</style></head><body><font size=-1>'
    for i in items[:-number-1:-1]:
        title = i.find("title").text
        pubDate = datetime(*strptime(i.find("pubDate").text,"%a, %d %b %Y %H:%M:%S %Z")[0:6])
	outDate = pubDate.strftime("[%d %b %Y]")
	###link = i.find("link").text
        description = i.find("description").text
        print "<li><b>%(outDate)s, %(title)s</b>: %(description)s</li>""" % locals()

	



    print "</font></body></html>"
    return tree

if __name__ == "__main__":
    import sys
    main (sys.argv)
