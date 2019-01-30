# Groovy Voicing Generation
Groovy project which takes chord progressions, voices them, and uses [lilypond](http://lilypond.org/) to output finished sheet music. 

## HLML
A DSL created with Groovy which forms the rules for the voice leading. Test.hlml is given as an example.

## Future features
A backtracking algorithm to add support for variation rules, for example a {Root, Root, Third, Fifth} voicing would be preffered but if one is impossible then {Root, Fifth, Third, Third} could be used.

## Dependencies
[jmusic1.6.4+](https://sourceforge.net/projects/jmusic/)
