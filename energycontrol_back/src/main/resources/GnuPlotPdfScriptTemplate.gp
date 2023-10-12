set size ratio 0.71 # this is the ratio of a DIN A4 page (21/29.7)
set terminal pdf enhanced size 16cm,8cm font ", 10"
set encoding iso_8859_1
set style data linespoints
set xlabel "Hora del día"
set ylabel 'kWh'
set grid
set key outside
set title '_TittleOfGraph_'
set output "_OutputFileName_"
plot '_DatFileName_'  using 1:2:xtic(1) w l title columnheader, '' using 1:3:xtic(1)  w l title columnheader