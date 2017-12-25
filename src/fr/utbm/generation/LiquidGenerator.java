package fr.utbm.generation;

import java.util.ArrayList;


public class LiquidGenerator extends PseudoRandom {
	public LiquidGenerator(double seed, long M) {
		super(seed, M);
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<ArrayList<Integer>> caveLiquidGen(ArrayList<ArrayList<Integer>> caves, int maxHeight, int minHeight, int liquidFrequence) {
		/*
		Entr�e: tableau de cave du cave generator (avec 0 et 1), hauteur maxi d'eau dans les caves, hauteur mini d'eau dans les caves, fr�quence de cave avec eau(�dit�)
		Sortie: tableau de caves avec 0 et 1 + 2 pour les liquids
		Algo:
		1- traverser le tableau
		2- d�s qu'on trouve un bloc 0 (vide) qui n'est pas marqu�, on marque tous les blocs 0 de sa grotte (lui inclus) dans un tableau temporaire ou autre si 
			tu trouves mieux (sans doute en r�cursif)
		3- on regarde avec al�atoire si oui ou non on doit remplir cette cave d'eau (en utilisant bien s�r le pseudo random h�rit�)
		4- on obtient la hauteur totale de la grotte (bloc le plus haut - bloc le plus bas)(�dit�)
		5- on calcule la hauteur al�atoire � remplir en fonction du min et max
		6- on met des 2 sur tous les blocs en dessous de cette hauteur */
		
		
		return caves;
	}
	
	
	public int[] surfaceLiquidGen(ArrayList<Integer> surface, int maxWidth, int minWidth, double liquidFrequence) {
		int[] liquids = new int[surface.size()];
		
		recursiveSurfaceLiquidGen(liquids, surface, maxWidth, minWidth, liquidFrequence, 0);
		return liquids;
	}
	
	private void recursiveSurfaceLiquidGen(int[] liquids, ArrayList<Integer> surface, int maxWidth, int minWidth, double liquidFrequence, int i) {
		while (i<surface.size()) { //On place i au d�but d'une pente descendante
			int height = surface.get(i);
			
			int j=i+1;
			while(j<surface.size() && surface.get(j) < surface.get(i)) { //On augmente j le long de la pente
				j++;
			}
			
			if (j>i+minWidth) { //On a une descente assez large
				if (j>i+maxWidth) { //On est trop large, on recommence en avan�ant i
					recursiveSurfaceLiquidGen(liquids, surface, maxWidth, minWidth, liquidFrequence, i+1);
					i=Integer.MAX_VALUE;
				}
				else
				{
					if (j==surface.size()) { //La descente n'est pas acceptable on est � droite, on recommence en avan�ant i
						recursiveSurfaceLiquidGen(liquids, surface, maxWidth, minWidth, liquidFrequence, i+1);
						i=Integer.MAX_VALUE;
					}
					else
					{
						if(super.getNextRandom()+0.5<liquidFrequence/100) {
							for (int k=i+1; k<j; k++) {
								liquids[k] = height-surface.get(k);
							}
						}
					}
				}
			}
			
			if (i != Integer.MAX_VALUE) {
				i=j;
			}
		}
	}
}
