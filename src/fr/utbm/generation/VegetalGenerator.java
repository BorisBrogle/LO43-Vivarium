package fr.utbm.generation;

import java.util.ArrayList;

import fr.utbm.biome.Biome;

public class VegetalGenerator extends PseudoRandom 
{

	public VegetalGenerator(double seed, long M) 
	{
		super(seed, M);
	}
	
	public ArrayList<Integer> surfaceVegetalGen(ArrayList<Biome> biomeList, ArrayList<Integer> surface,int[] surfaceLiquid)
	{
		/*
		 * Il faut 3 blocks de large sans pente pour pouvoir y placer un v�g�tal
		 * On parcourt la liste de biomes (for biome b: biomeList), sachant que pour chaque biome, on a acc�s:
		 * - � la largeur du biome (b.getWidth() : int)
		 * - � une arrayList d'id et frequence  (b.getVegetalIdFrequence(): ArrayList<int[]>)
		 * � la fin on retourne une arrayList d'ID de plante
		 * pour chaque block de la surface : 0 si pas de plante, ID de la plante sinon
		 */
		
		ArrayList<Integer> vegetalList = new ArrayList<Integer>(); // � renvoyer
		int sumBiomeLength=0; // pour ajuster l'index de surface
		
		//on traverse les biomes
		for(Biome b : biomeList)
		{
			int hauteur = 0; //pour voir si on a un terrain plat
			//on traverse les blocs du biome b
			for(int i=0; i<b.getWidth(); i++)
			{
				//on test si on est sur les bords de map (qui contiennent du verre) ou si on est sur de l'eau
				if(i+sumBiomeLength<1 || i+sumBiomeLength>surface.size()-2 || surfaceLiquid[i+sumBiomeLength]!=0)
				{
					vegetalList.add(0);
				}
				else
				{
					hauteur = surface.get(i+sumBiomeLength);
					boolean hasPlace = true; //pour savoir si il y a la place d'ajouter un vegetal
					boolean vegetalAdded = false; //pour savoir si on a ajout� une plante
					boolean tryToAddVegetal = false; //pour savoir si on a tent� d'ajouter une plante (pour ne pas essayer d'en ajouter une autre)
					
					double randomVegetal = (super.getNextRandom()+0.5)*100; //random entre 0 et 100
					int j=0; //pour parcourir les ID des plantes
					
					//tant qu'on a pas tent� d'ajouter une plante et qu'on a pas atteint la fin
					while(tryToAddVegetal== false && j<b.getVegetalIdFrequence().size())
					{
						if(randomVegetal > b.getVegetalIdFrequence().get(j)[1]) //random frequence > frequence de la plante j
						{
							randomVegetal -= b.getVegetalIdFrequence().get(j)[1]; //pour toujours travailler sur un intervalle de [0-fr�quence]
						}
						else //on est dans la bonne range, on v�rifie qu'on a la place de l'ajouter
						{
							tryToAddVegetal=true;
							
							/*on v�rifie: 
							 *- si le nombre de blocs requis pour la plante est � la bonne hauteur (on a une surface plate)
							 *- si la de la plante ne d�passe pas du biome
							 *si la de la plante ne d�passe pas de la map (-2 pour le verre)
							 */
							for(int k=1; k<b.getVegetalIdFrequence().get(j)[2]; k++)
							{
								if(i+k>b.getWidth()-1 || i+sumBiomeLength+k>surface.size()-2 || hauteur!=surface.get(i+sumBiomeLength+k)) 
								{
									hasPlace=false;
								}
							}
							
							if(hasPlace)
							{					
								vegetalList.add(b.getVegetalIdFrequence().get(j)[0]); // on ajoute l'index de la plante � la liste
								vegetalAdded = true; //on a ajout� une plante
								
								//on ajoute la place necessaire � la plante-1 blocs sur lesquels on ne peut pas ajouter de plante (ID=0)
								//ce qui correspond � la taille de la plante � l'�cran
								for(int k=1; k<b.getVegetalIdFrequence().get(j)[2]; k++)
								{
									if(i+k<b.getWidth() && i+sumBiomeLength+k<surface.size()) //pour ne pas ajouter plus que la taille de la carte
									{
										vegetalList.add(0);
										i++;
									}
								}
							}
						}
						
						j++;
					}
					
					if(vegetalAdded==false)
					{
						vegetalList.add(0); // on indique qu'il n'y a pas de plante sur ce bloc
					}
				}
			}
			sumBiomeLength+=b.getWidth(); //on ajoute la taille du biome parcouru � la taille totale des biomes parcourus
		}

		return vegetalList;
	}

}