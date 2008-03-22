Files, scripts and packages in this directory are used to generate
specific fonts for a SuperWaba Project.

This was/is used to generate Japanese fonts for the BT747 project
at http://sf.net/projects/bt747 .
The entry format is a TrueType font.


===================
Flow
===================
1. Run Japanese.sh .
   This script generates the 'profile'.   A profile defines the
   characters to include in a font file.
2. Execute the graphical interface (using runFontizer.bat or .sh).
2.a.Load the 'sazanami-gothic.ttf' font.
  b.Load the 'myJap.profile' created in first step.
  b.Change 'Ascend.' to 14 instead of the default '10'
    Adjust other parameters too (Descent to 5).
    => These parameters must be confirmed later.
  c.Moved the generated 'pdb' file to 'UFFJap_H.pdb'.
  
The UFFJap_H.pdb file must then be made available for the SuperWaba
application.
