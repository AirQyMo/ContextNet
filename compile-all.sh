echo "#-----------------# Compiling auxiliares #-----------------#"
bash scripts/compile-aux.sh

# echo "#-----------------# Compiling group-definer #-----------------#"
# bash scripts/compile-gd.sh

echo "#-----------------# Compiling processing-node #-----------------#"
bash scripts/compile-pn.sh

echo "#-----------------# Compiling mobile-node #-----------------#"
bash scripts/compile-mn.sh
