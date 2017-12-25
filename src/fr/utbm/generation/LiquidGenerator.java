package fr.utbm.generation;

import java.util.ArrayList;

import fr.utbm.biome.Biome;

public class LiquidGenerator extends PseudoRandom {

	public LiquidGenerator(double seed, long M) {
		super(seed, M);
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<Integer> surfaceLiquidGen(ArrayList<Biome> biomeList, ArrayList<Integer> surface) {
		
		
		
		return surface;
	}
	
	public ArrayList<ArrayList<Integer>> caveLiquidGen(ArrayList<ArrayList<Integer>> caves, int maxHeight, int minHeight, int liquidFrequence) {
		/*
		Entr�e: tableau de cave du cave generator (avec 0 et 1), hauteur maxi d'eau dans les caves, hauteur mini d'eau dans les caves, fr�quence de cave avec eau(�dit�)
		Sortie: tableau de caves avec 0 et 1 + 2 pour les liquids
		Algo:
		1- traverser le tableau
		2- d�s qu'on trouve un bloc 0 (vide) qui n'est pas marqu�, on marque tous les blocs 0 de sa grotte (lui inclus) dans un tableau temporaire ou autre si tu trouves mieux
		KyStolos - Hier � 17:00
		3- on regarde avec al�atoire si oui ou non on doit remplir cette cave d'eau (en utilisant bien s�r le pseudo random h�rit�)
		4- on obtient la hauteur totale de la grotte (bloc le plus haut - bloc le plus bas)(�dit�)
		5- on calcule la hauteur al�atoire � remplir en fonction du min et max
		6- on met des 2 sur tous les blocs en dessous de cette hauteur */
		
		
		return caves;
	}
}
