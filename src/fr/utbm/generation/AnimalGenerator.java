package fr.utbm.generation;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

import fr.utbm.biome.Biome;
import fr.utbm.world.Chunk;
import fr.utbm.world.Map;

public class AnimalGenerator extends PseudoRandom {

	private ArrayList<int[]> animalList; //contient l'ensemble des animaux existants dans notre programme

	public AnimalGenerator(double seed, long M) {
		super(seed, M);
		initializeList();
	}

	/* On initialise une liste (animalList) contenant l'ensemble des animaux existants dans notre programme comme suit:
	 * animalList.get(0)[0]: l'id de l'entit�
	 * animalList.get(0)[1]: sa fr�quence d'apparition � la surface
	 * animalList.get(0)[2]: sa fr�quence d'apparition dans les grottes (enfer compris)
	 * animalList.get(0)[3]: sa taille en largeur (la place n�cessaire pour qu'il puisse apparaitre)
	 * animalList.get(0)[4]: sa taille en hauteur
	 * animalList.get(0)[5]: le biome dans lequel il apparait (-1 si il peut apparaitre ind�pendamment du biome)
	 * animalList.get(0)[6]: si on peut le trouver en surface ou non (0: non, 1: oui)
	 * animalList.get(0)[7]: si on peut le trouver dans les grottes ou non (0: non, 1: oui)
	 * animalList.get(0)[8]: si on peut le trouver en enfer ou non (0: non, 1: oui)
	 * animalList.get(0)[9]: si il vole ou pas (0: terrestre, 1: a�rien)
	 * animalList.get(0)[10]: si si il peut nager dans l'eau ou non (0: non, 1: oui)
	 * animalList.get(0)[11]: si si il peut nager dans la lave ou non (0: non, 1: oui)
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
			
			int[] tmp = new int[12];
			tmp[0] = Integer.parseInt(child.get("id"));
			tmp[1] = Integer.parseInt(child.get("surfaceFrequence"));
			tmp[2] = Integer.parseInt(child.get("caveFrequence"));
			tmp[3] = Integer.parseInt(child.get("widthNeeded"));
			tmp[4] = Integer.parseInt(child.get("heightNeeded"));
			tmp[5] = Integer.parseInt(child.get("biomeID"));
			tmp[6] = Integer.parseInt(child.get("surface"));
			tmp[7] = Integer.parseInt(child.get("cave"));
			tmp[8] = Integer.parseInt(child.get("hell"));
			tmp[9] = Integer.parseInt(child.get("flying"));
			tmp[10] = Integer.parseInt(child.get("water"));
			tmp[11] = Integer.parseInt(child.get("lava"));
			
			this.animalList.add(tmp);
		}
	}
	
	private ArrayList<int[]> getCaveAnimals()
	{
		ArrayList<int[]> caveAnimalList = new ArrayList<>();
		
		for(int[] animal : this.animalList)
		{
			if(animal[7]==1)
			{
				caveAnimalList.add(animal);
			}
		}
		
		return caveAnimalList;
	}
	
	private ArrayList<int[]> getHellAnimals()
	{
		ArrayList<int[]> hellAnimalList = new ArrayList<int[]>();
		
		for(int[] animal : this.animalList)
		{
			if(animal[8]==1)
			{
				hellAnimalList.add(animal);
			}
		}
		
		return hellAnimalList;
	}
	
	private ArrayList<int[]> getFlyingAnimals()
	{
		ArrayList<int[]> flyingAnimalList = new ArrayList<int[]>();
		
		for(int[] animal : this.animalList)
		{
			if(animal[9]==1)
			{
				flyingAnimalList.add(animal);
			}
		}
		
		return flyingAnimalList;
	}
	
	//pour savoir si l'animal aquatique est dans l'eau ou si l'animal terrestre est sur le sol � cette position
	private boolean waterTest(int[] animal, int[] surfaceLiquid, int i)
	{
		if(animal[10]==0 && surfaceLiquid[i]==0)
		{
			return true;
		}
		else if(animal[10]==1 && surfaceLiquid[i]!=0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean hasPlaceTest(int[] animal, ArrayList<Integer> surface, Biome b, int hauteur, int i, int sumBiomeLength)
	{
		/*on v�rifie: 
		 *- si le nombre de blocs requis pour l'animal est � la bonne hauteur (on a une surface plate)
		 *- si l'animal ne d�passe pas du biome
		 *- si l'animal ne d�passe pas de la map (-2 pour le verre)
		 */
		for(int k=1; k<animal[3]; k++)
		{
			if(i+k>b.getWidth()-1 || i+sumBiomeLength+k>surface.size()-2 || hauteur!=surface.get(i+sumBiomeLength+k)) 
			{
				return false;
			}
		}
		return true;
	}
	

	public ArrayList<int[]> surfaceAnimalGen(ArrayList<Biome> biomeList, ArrayList<Integer> surface, int[] surfaceLiquid)
	{
		/*
		 * Il faut "placeNeeded" blocks de large sans pente pour pouvoir y placer un animal
		 * On parcourt la liste de biomes (for biome b: biomeList)
		 * On choisit un animal al�atoirement dans la liste de tous les animaux
		 * On test si l'animal requiert un biome sp�cifique
		 * On ajoute l'animal � la liste si n�cessaire
		 * � la fin on retourne une arrayList d'ID d'animaux (terrestres en [0] et a�riens en [1])
		 * pour chaque block de la surface : 0 si pas d'animal, ID de l'animal sinon
		 */
		
		ArrayList<int[]> surfaceAnimal = new ArrayList<>(); // � renvoyer
		ArrayList<int[]> flyingAnimalList = getFlyingAnimals(); //liste des animaux volants
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
					surfaceAnimal.add(new int[3]);
				}
				else
				{
					surfaceAnimal.add(new int[3]);
					
					/*Animaux volants*/
					if(!flyingAnimalList.isEmpty()) //si il existe des animaux volants
					{
						int[] randomFlyingAnimal = flyingAnimalList.get((int)((super.getNextRandom()+0.5)*(flyingAnimalList.size()))); //animal al�atoire parmi les animaux volants
						double randomFlyingAnimalFrequence = (super.getNextRandom()+0.5)*100; //frequence aleatoire entre 0 et 100
						
						if(randomFlyingAnimalFrequence<=randomFlyingAnimal[1])
						{
							//variable al�atoire entre la surface+10 (pour ne pas apparaitre sur un autre animal) et la maxHeight-1
							int randomFlyingAnimalHeight = (int)((Chunk.CHUNK_HEIGHT-(1+10+Map.LIMIT_SURFACE+MapGenerator.DIRT_SURFACE+surface.get(i+sumBiomeLength)+surfaceLiquid[i+sumBiomeLength]+1))*(super.getNextRandom()+0.5)+10); 
							surfaceAnimal.get(i+sumBiomeLength)[1] = randomFlyingAnimal[0];
							surfaceAnimal.get(i+sumBiomeLength)[2] = randomFlyingAnimalHeight;
						}
					}
					else //il n'existe pas d'animaux volants
					{
						surfaceAnimal.add(new int[3]);
					}
						
					/*Animaux terrestres et aquatiques*/
					hauteur = surface.get(i+sumBiomeLength);
					int[] randomAnimal = animalList.get((int)((super.getNextRandom()+0.5)*(animalList.size()))); //animal al�atoire parmi les animaux existants
					double randomAnimalFrequence = (super.getNextRandom()+0.5)*100; //frequence aleatoire entre 0 et 100
					
					if(randomAnimal[6]==1 //c'est un animal de la surface
						&& randomAnimalFrequence<=randomAnimal[1] //la fr�quence al�atoire est dans la bonne range
						&& hasPlaceTest(randomAnimal, surface, b, hauteur, i, sumBiomeLength) //il a la place d'apparaitre 
						&& (randomAnimal[5]==-1 || randomAnimal[5]==b.getId()) //il n'a pas de biome attitr� ou est dans le bon biome
						&& waterTest(randomAnimal, surfaceLiquid, i+sumBiomeLength)) //est dans l'eau si il est aquatique, en dehors de l'eau sinon
					{
						surfaceAnimal.get(i+sumBiomeLength)[0]=randomAnimal[0]; // on ajoute l'index de l'animal � la liste
						//on ajoute la place necessaire � l'animal-1 blocs sur lesquels on ne peut pas ajouter d'animal (ID=0)
						//ce qui correspond � la taille de l'animal � l'�cran
						for(int k=1; k<randomAnimal[3]; k++)
						{
							if(i+k<b.getWidth() && i+sumBiomeLength+k<surface.size()) //pour ne pas ajouter plus que la taille de la carte
							{
								surfaceAnimal.add(new int[3]);
								i++;
							}
						}
					}
				}
			}
			sumBiomeLength+=b.getWidth(); //on ajoute la taille du biome parcouru � la taille totale des biomes parcourus
		}

		return surfaceAnimal;
	}

	public ArrayList<ArrayList<Integer>> caveEntityGen(ArrayList<ArrayList<Integer>> caves, ArrayList<Integer> surface) {
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
		
		//tableau temporaire pour savoir si on a d�j� trait� une case ou non (0: vide, 1:animal)
		int[][] tmpArray = new int[caves.size()][caves.get(0).size()];
		// Liste � 2 dimensions avec 0 si pas d'animal, ID de l'animal sinon
		ArrayList<ArrayList<Integer>> caveAnimals = new ArrayList<>();
		ArrayList<int[]> caveAnimalList = getCaveAnimals();
		ArrayList<int[]> hellAnimalList = getHellAnimals();
		/*
		 * on traverse le tableau de cave du cave generator (avec 0, 1 et 2)
		 * si c'est vide et qu'il y a du sol en dessous, on regarde si il y a la place de placer un animal choisi au hasard
		 */
		for (int i = 0; i < caves.size(); i++) {
			caveAnimals.add(new ArrayList<Integer>());
			for (int j = 0 ; j < caves.get(i).size(); j++) 
			{
				//on est sur le verre, donc pas d'animal
				if(j<1 || i<1 || i>caves.size()-2)
				{
					caveAnimals.get(i).add(0);
				}
				else if(tmpArray[i][j]!=1) //on a pas encore d'animal dans cette case donc on peut tenter d'en ajouter un
				{
					/*CaveAnimal*/
					if(j>Map.LIMIT_CAVE)
					{
						if(!caveAnimalList.isEmpty()) //si on a des animaux dans les grottes
						{
							int[] randomCaveAnimal = caveAnimalList.get((int)((super.getNextRandom()+0.5)*(caveAnimalList.size()))); //animal al�atoire parmi les animaux des grottes
							double randomCaveAnimalFrequence = (super.getNextRandom()+0.5)*100; //frequence aleatoire entre 0 et 100
							testAndAddAnimal(randomCaveAnimal, randomCaveAnimalFrequence, caves, surface, caveAnimals, tmpArray, i, j);
						}
						else //on a pas d'animaux dans les grottes
						{
							caveAnimals.get(i).add(0);
						}
					}
					else /*HellAnimal*/
					{
						if(!hellAnimalList.isEmpty()) //si on a des animaux en enfer
						{
							int[] randomHellAnimal = hellAnimalList.get((int)((super.getNextRandom()+0.5)*(hellAnimalList.size()))); //animal al�atoire parmi les animaux de l'enfer
							double randomHellAnimalFrequence = (super.getNextRandom()+0.5)*100; //frequence aleatoire entre 0 et 100
							testAndAddAnimal(randomHellAnimal, randomHellAnimalFrequence, caves, surface, caveAnimals, tmpArray, i, j);
						}
						else //on n'a pas d'animaux en enfer
						{
							caveAnimals.get(i).add(0);
						}
					}
				}
				else //on a d�j� une partie d'animal dans cette case donc on ne peut pas en faire apparaitre un autre ici
				{
					caveAnimals.get(i).add(0);
				}
			}
		}

		return caveAnimals;
	}
	
	private void testAndAddAnimal(int[] randomAnimal, double randomAnimalFrequence, ArrayList<ArrayList<Integer>>caves, ArrayList<Integer> surface, ArrayList<ArrayList<Integer>> caveAnimals, int[][] tmpArray, int i , int j)
	{
		if(randomAnimalFrequence<=randomAnimal[2])//[2]: fr�quence d'apparition de l'animal dans les grottes
		{
			if (randomAnimal[10]!=1 && randomAnimal[11]!=1) //si il n'est pas aquatique (eau, lave)
			{
				if(isOnGroundAndHasPlace(randomAnimal[3], randomAnimal[4], caves, surface, i, j)) //on v�rifie qu'il est sur le sol et qu'il a la place d'apparaitre
				{
					caveAnimals.get(i).add(randomAnimal[0]); //on ajoute son ID � la liste
					for(int k=0; k<randomAnimal[3]; k++)
					{
						for(int l=0; l<randomAnimal[4]; l++)
						{
							tmpArray[i+k][j+l]=1; //on indique que ces cases ont d�j� �t� trait�es (par rapport � la taille de l'animal)
						}
					}
				}
				else
				{
					caveAnimals.get(i).add(0);
				}
			}
			else //il est aquatique
			{
				if(isInLiquidAndHavePlace(randomAnimal[3], randomAnimal[4], caves, surface, i, j)) //on v�rifie qu'il est dans l'eau et qu'il a la place d'apparaitre
				{
					caveAnimals.get(i).add(randomAnimal[0]);
					for(int k=0; k<randomAnimal[3]; k++)
					{
						for(int l=0; l<randomAnimal[4]; l++)
						{
							tmpArray[i+k][j+l]=1; //on indique que ces cases ont d�j� �t� trait�es (par rapport � la taille de l'animal)
						}
					}
				}
				else
				{
					caveAnimals.get(i).add(0);
				}
			}
		}
		else //on choisit de ne pas ajouter d'animal
		{
			caveAnimals.get(i).add(0);
		}
	}
	
	private boolean isOnGroundAndHasPlace(int widthNeeded, int heightNeeded, ArrayList<ArrayList<Integer>> caves, ArrayList<Integer> surface, int i, int j)
	{		
		for(int k=0; k<widthNeeded; k++)
		{
			int maxHeightDiff = 0;
			if(surface.get(i)<0)
			{
				maxHeightDiff = surface.get(i)-1; //variations de surface
			}
			
			for(int l=0; l<heightNeeded; l++)
			{
				//si on d�passe ou qu'il n'y a pas assez d'air ou qu'il n'y a pas assez de sol
				if(i+k>caves.size()-1 || j+l>caves.get(0).size()+maxHeightDiff || caves.get(i+k).get(j+l)!=0 || caves.get(i+k).get(j+l+1)!=0 || caves.get(i+k).get(j-1)!=1)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isInLiquidAndHavePlace(int widthNeeded, int heightNeeded, ArrayList<ArrayList<Integer>> caves, ArrayList<Integer> surface, int i, int j)
	{		
		for(int k=0; k<widthNeeded; k++)
		{
			int maxHeightDiff = 0;
			if(surface.get(i)<0)
			{
				maxHeightDiff = surface.get(i)-1; //variations de surface
			}
			
			for(int l=0; l<heightNeeded; l++)
			{
				//si on d�passe ou qu'il n'y a pas de d'eau
				if(i+k>caves.size()-1 || j+l>caves.get(0).size()+maxHeightDiff || caves.get(i+k).get(j+l)!=2)
				{
					return false;
				}
			}
		}
		return true;
	}

}
