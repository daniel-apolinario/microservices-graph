DIR=/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia
FILES=$DIR/*
SUBFILES=$DIR/**/*

rm -f $DIR/grafo*.png

if [ $# -eq 1 ] 
then
   for f in $1/*
   do
   if [[ "$f" == *\.dot ]]
   then
     echo $f
     dot -Tpng "$f" -o  $(dirname "$f")/$(basename "$f" .dot).png
   fi
   done

else

   for f in $FILES $SUBFILES
   do
   if [[ "$f" == *\.dot ]]
   then
     dot -Tpng "$f" -o  $(dirname "$f")/$(basename "$f" .dot).png
   fi
   done
fi
