package fr.utbm.generation;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

import fr.utbm.biome.Biome;
import fr.utbm.entity.Entity;

public class AnimalGenerator extends PseudoRandom {

	private ArrayList<int[]> animalList; //contient l'ensemble des animaux existants dans notre programme

	public AnimalGenerator(double seed, long M) {
		super(seed, M);
		initializeList();
	}

	/* On initialise une liste (animalList) contenant l'ensemble des animaux existants dans notre programme comme suit:
	 * animalList.get(0)[0]: l'id de l'entit�
	 * animalList.get(0)[1]: sa fr�quence d'apparition
	 * animalList.get(0)[2]: sa taille en largeur (la place n�cessaire pour qu'il puisse apparaitre)
	 * animalList.get(0)[3]: le biome dans lequel il apparait (-1 si il peut apparaitre ind�pendamment du biome)
	 * animalList.get(0)[4]: si on peut le trouver en surface ou non (0: non, 1: oui)
	 * animalList.get(0)[5]: si on peut le trouver dans les grottes ou non (0: non, 1: oui)
	 * animalList.get(0)[6]: si on peut le trouver en enfer ou non (0: non, 1: oui)
	 * animalList.get(0)[7]: si il vole ou pas (0: terrestre, 1: a�rien)
	 * animalList.get(0)[8]: si si il peut nager dans l'eau ou non (0: non, 1: oui)
	 * animalList.get(0)[9]: si si il peut nager dans la lave ou non (0: non, 1: oui)
	 * Cette liste est initialis�e � partir du entityManager.xml
	 */
	private void initializeList() {

		this.animalList = new ArrayList<int[]>();
		
		String xmlFile = "res/animalManager.xml";
		XmlReader reader = new XmlReader();
		Element root = null;

		try {
			root = reader.parse(Gdx.files.internal(xmlFile));
		} catch (IOException e) {
			System.out.println("ANIMAL-Initialisation : Erreur chargement animalManager.xml");
			e.printStackTrace();
		}
		Array<Element> entityAnimals = root.getChildrenByName("animal");
		for (Element child : entityAnimals) {
			
			int[] tmp = new int[10];
			tmp[0] = Integer.parseInt(child.get("id"));
			tmp[1] = Integer.parseInt(child.get("frequence"));
			tmp[2] = Integer.parseInt(child.get("placeNeeded"));
			tmp[3] = Integer.parseInt(child.get("biomeID"));
			tmp[4] = Integer.parseInt(child.get("surface"));
			tmp[5] = Integer.parseInt(child.get("cave"));
			tmp[6] = Integer.parseInt(child.get("hell"));
			tmp[7] = Integer.parseInt(child.get("flying"));
			tmp[8] = Integer.parseInt(child.get("water"));
			tmp[9] = Integer.parseInt(child.get("lava"));
			
			this.animalList.add(tmp);
		}
	}
	
	//pour savoir si l'animal aquatique est dans l'eau ou si l'animal terrestre est sur le sol � cette position
	private boolean waterTest(int[] animal, int[] surfaceLiquid, int i)
	{
		if(animal[8]==0 && surfaceLiquid[i]==0)
		{
			return true;
		}
		else if(animal[8]==1 && surfaceLiquid[i]!=0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean placeTest(int[] animal, ArrayList<Integer> surface, Biome b, int hauteur, int i, int sumBiomeLength)
	{
		/*on v�rifie: 
		 *- si le nombre de blocs requis pour l'animal est � la bonne hauteur (on a une surface plate)
		 *- si l'animal ne d�passe pas du biome
		 *- si l'animal ne d�passe pas de la map (-2 pour le verre)
		 */
		for(int k=1; k<animal[2]; k++)
		{
			if(i+k>b.getWidth()-1 || i+sumBiomeLength+k>surface.size()-2 || hauteur!=surface.get(i+sumBiomeLength+k)) 
			{
				return false;
			}
		}
		return true;
	}
	

	public ArrayList<Integer> surfaceAnimalGen(ArrayList<Biome> biomeList, ArrayList<Integer> surface, int[] surfaceLiquid)
	{
		/*
		 * Il faut "placeNeeded" blocks de large sans pente pour pouvoir y placer un animal
		 * On parcourt la liste de biomes (for biome b: biomeList)
		 * On choisit un animal al�atoirement dans la liste de tous les animaux
		 * On test si l'animal requiert un biome sp�cifique
		 * On ajoute l'animal � la liste si n�cessaire
		 * � la fin on retourne une arrayList d'ID d'animaux
		 * pour chaque block de la surface : 0 si pas d'animal, ID de l'animal sinon
		 */
		
		ArrayList<Integer> surfaceAnimal = new ArrayList<Integer>(); // � renvoyer
		int sumBiomeLength=0; // pour ajuster l'index de surface
		
		//on traverse les biomes
		for(Biome b : biomeList)
		{
			int hauteur = 0; //pour voir si on a un terrain plat
			//on traverse les blocs du biome b
			for(int i=0; i<b.getWidth(); i++)
			{
				//on test si on est sur les bords de map (qui contiennent du verre)
				if(i+sumBiomeLength<1 || i+sumBiomeLength>surface.size()-2)
				{
					surfaceAnimal.add(0);
				}
				else
				{
					hauteur = surface.get(i+sumBiomeLength);
					int[] randomAnimal = animalList.get((int)((super.getNextRandom()+0.5)*(animalList.size()-1))); //animal al�atoire parmi les animaux existants
					double randomAnimalFrequence = (super.getNextRandom()+0.5)*100; //frequence aleatoire entre 0 et 100
										
					/*si on est dans la bonne plage de fr�quence
					 * 
					 */
					if(randomAnimal[4]==1 //il est � la surface
						&& randomAnimalFrequence<=randomAnimal[1] //la fr�quence al�atoire est dans la bonne range
						&& placeTest(randomAnimal, surface, b, hauteur, i, sumBiomeLength) //il a la place d'apparaitre 
						&& (randomAnimal[3]==-1 || randomAnimal[3]==b.getId()) //il n'a pas de biome attitr� ou est dans le bon biome
						&& waterTest(randomAnimal, surfaceLiquid, i+sumBiomeLength)) //est dans l'eau si il est aquatique, en dehors de l'eau sinon
					{
						surfaceAnimal.add(randomAnimal[0]); // on ajoute l'index de l'animal � la liste
						//on ajoute la place necessaire � l'animal-1 blocs sur lesquels on ne peut pas ajouter d'animal (ID=0)
						//ce qui correspond � la taille de l'animal � l'�cran
						for(int k=1; k<randomAnimal[2]; k++)
						{
							if(i+k<b.getWidth() && i+sumBiomeLength+k<surface.size()) //pour ne pas ajouter plus que la taille de la carte
							{
								surfaceAnimal.add(0);
								i++;
							}
						}
					}
					else
					{
						surfaceAnimal.add(0);
					}
				}
			}
			sumBiomeLength+=b.getWidth(); //on ajoute la taille du biome parcouru � la taille totale des biomes parcourus
		}

		return surfaceAnimal;
	}

	public ArrayList<Entity> caveEntityGen(ArrayList<ArrayList<Integer>> caves, ArrayList<Entity> entities) {
		/*
		 * Entr�e: tableau de cave du cave generator (avec 0 et 1), liste des
		 * entit�s pouvant �tre pr�sentes Sortie: liste des entit�s � ajouter
		 * Algo: 1- traverser le tableau 2- d�s qu'on trouve un bloc 0 (vide)
		 * qui n'est pas marqu�, on marque tous les blocs 0 de sa grotte (lui
		 * inclus) dans un tableau temporaire ou autre si tu trouves mieux (sans
		 * doute en r�cursif) 3- on regarde avec al�atoire pour chaque entit� si
		 * oui ou non on doit la mettre dans la grotte (fr�quence d'apparition
		 * ?) 4- on v�rifie si on a la place dans cette grotte de mettre
		 * l'entit� en question
		 */

		// tableau temporaire pour savoir si on a d�j� trait� une case ou non
		int[][] tmpArray = new int[caves.size()][caves.get(0).size()];
		ArrayList<Entity> createdEntities = new ArrayList<Entity>();

		/*
		 * on traverse le tableau de cave du cave generator (avec 0 et 1) si le
		 * block est marqu� 1, on ajoute met pareil dans le tmpArray sinon on
		 * lance l'algo r�cursif qui doit remplir la cave correspondante de
		 * liquide
		 */
		for (int i = 0; i < caves.size(); i++) {
			for (int j = caves.get(i).size() - 1; j >= 0; j--) {
				if (caves.get(i).get(j) == 1) {
					tmpArray[i][j] = 1;
				} else if (caves.get(i).get(j) == 0 && tmpArray[i][j] == 0) {
					// hauteur d'air dans les grottes al�atoire entre minHeight
					// et maxHeight
					int doesItSpawn = (int) (super.getNextRandom() + 0.5);
					recursiveCaveLiquidGen(caves, tmpArray, entities, createdEntities, i, j, doesItSpawn);
				}
			}
		}

		return createdEntities;
	}

	private void recursiveCaveLiquidGen(ArrayList<ArrayList<Integer>> caves, int[][] tmpArray,
			ArrayList<Entity> entities, ArrayList<Entity> createdEntities, int i, int j, int doesItSpawn) {
		tmpArray[i][j] = -1; // signifie qu'on a trait� ce block

		// si la proba de spawn>does it spawn, on appelle la m�thode sur le
		// block suivant
		if (doesItSpawn > entities.get(0).getPosX()) {
			// on transmet en dessous
			if ((j > 0) && (caves.get(i).get(j - 1) == 0) && (tmpArray[i][j - 1] == 0)) {
				recursiveCaveLiquidGen(caves, tmpArray, entities, createdEntities, i, j, doesItSpawn);
			}
			// on transmet � gauche
			else if ((i > 0) && (caves.get(i - 1).get(j) == 0) && (tmpArray[i - 1][j] == 0)) {
				recursiveCaveLiquidGen(caves, tmpArray, entities, createdEntities, i, j, doesItSpawn);
			}
			// on transmet � droite
			else if ((i < caves.size() - 1) && (caves.get(i + 1).get(j) == 0) && (tmpArray[i + 1][j] == 0)) {
				recursiveCaveLiquidGen(caves, tmpArray, entities, createdEntities, i, j, doesItSpawn);
			}
		}
		// si height<=0, on remplit la case de liquide et on appelle la m�thode
		// sur les voisins
		else {
			caves.get(i).set(j, 2); // le block en (i,j) sera un block d'eau

			// on transmet en dessous
			if ((j > 0) && (caves.get(i).get(j - 1) == 0)) {
				recursiveCaveLiquidGen(caves, tmpArray, entities, createdEntities, i, j, doesItSpawn);
			}

			// on transmet � gauche
			if ((i > 0) && (caves.get(i - 1).get(j) == 0)) {
				recursiveCaveLiquidGen(caves, tmpArray, entities, createdEntities, i, j, doesItSpawn);
			}

			// on transmet � droite
			if ((i < caves.size() - 1) && (caves.get(i + 1).get(j) == 0)) {
				recursiveCaveLiquidGen(caves, tmpArray, entities, createdEntities, i, j, doesItSpawn);
			}
		}
	}

}
