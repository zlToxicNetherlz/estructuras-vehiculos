package me.itoxic.vehiculos;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Implementacion de un algoritmo para asignar vehiculos compartidos
 * Estructura de datos utilizada: Grafo con Matrices de Adyacencia
 * Complejidad: Peor Caso y Mejor Caso O(n*n)
 *
 * @author Abraham Miguel Lora Vargas
 * @author Juan Manuel Young
 * @version 2.4
 *
 */
public class FastPath {

    /**
     * Metodo para leer un archivo con los duenos de vehiculos y la empresa
     * Complejidad: Mejor y peor caso es O(n * n), donde n es son los duenos de vehiculos y la empresa
     *
     * @param pointsNumber El numero de puntos es 1 de la empresa y n - 1 de los duenos de vehiculos
     * @return Un grafo completo con la distancia mas corta entre todos los vertices.
     *
     */
    public static AdaptedGraph readFile(int pointsNumber, float p){

        final String nombreDelArchivo = "dataset-ejemplo-U=" + pointsNumber + "-p=" + p + ".txt";
        AdaptedGraph grafo = new AdaptedGraph(pointsNumber);

        try {

            BufferedReader br = new BufferedReader(new FileReader(nombreDelArchivo));
            String lineaActual = br.readLine();

            for (int i = 1; i <= 3; i++) // Descarta las primeras 3 lineas
                lineaActual = br.readLine();

            lineaActual = br.readLine();

            for (int i = 1; i <= pointsNumber; i++) //Descarta los nombres y coordenadas de los vertices
                lineaActual = br.readLine();

            for (int i = 1; i <= 3; i++) // Descarta las siguientes 3 lineas
                lineaActual = br.readLine();

            while (lineaActual != null){ // Mientras no llegue al fin del archivo. Lee la informacion de las aristas

                String[] cadenaParticionada = lineaActual.split(" ");

                if(cadenaParticionada[0] == "")
                    continue;

                if(cadenaParticionada.length >= 2 && cadenaParticionada[1] == "")
                    continue;

                if(cadenaParticionada.length >= 3 && cadenaParticionada[2] == "")
                    continue;

                grafo.addEdge(
                        Integer.parseInt(cadenaParticionada[0]) - 1,
                        Integer.parseInt(cadenaParticionada[1]) - 1,
                        Integer.parseInt(cadenaParticionada[2]));

                lineaActual = br.readLine();

            }

        }
        catch(IOException ioe) {
            System.out.println("Error leyendo el archivo de entrada: " + ioe.getMessage());
        }

        return grafo;

    }

    /**
     * Primera parte del método merge sort el cual se implemetara para ordenar el grafo, a través de los pesos de los arcos
     * de cada sucesor con el punto "pos".
     *
     * @param input Sucesores de un verice.
     * @param grafo Grafo completo con la distancia mas corta entre todos los vertices.
     * @param pos Verice origen o referencia.
     *
     */
    public static void sort(ArrayList<Integer> input, AdaptedGraph grafo, int pos) {

        if(input.size() < 2)  // Si es menor que 2 el tamaño no necesita ser organizado.
            return;

        int mid = input.size() / 2;

        ArrayList<Integer> left = new ArrayList<>(mid);
        ArrayList<Integer> right = new ArrayList(input.size() - mid);

        for(int i = 0; i < mid; i++) //copy left
            left.add(i, input.get(i));

        for(int i = 0; i < input.size() - mid; i++) //copy right
            right.add(i, input.get(mid+i));

        sort(left, grafo, pos);
        sort(right, grafo, pos);
        merge(left, right, input, grafo, pos);
    }

    /**
     * Segunda parte del método merge sort en el cual divide el arreglo de sucesores un dos arreglos hasta que los vaya
     * ubicando de menor a major.
     *
     * @param a
     * @param b
     * @param all
     * @param grafo
     * @param pos
     *
     */
    private static void merge(ArrayList<Integer> a, ArrayList<Integer> b, ArrayList<Integer> all, AdaptedGraph grafo, int pos){

        int i = 0, j = 0, k = 0;

        while(i < a.size() && j < b.size()) { //merge back

            if(grafo.getWeight(pos, a.get(i)) < grafo.getWeight(pos, b.get(j))) { // a.get(i) < b.get(j)
                all.set(k, a.get(i));//[k] = a[i];
                i++;
            } else {
                all.set(k, b.get(j));//[k] = b[j];
                j++;
            }

            k++;

        }

        while(i < a.size())//left remaining
            all.set(k++, a.get(i++)); //all[k++] = a[i++];

        while(j < b.size()) //right remaining
            all.set(k++, b.get(j++)); //all[k++] = b[j++];

    }

    public static float closingTime(AdaptedGraph grafo,int ver,float p){
        return grafo.getWeight(0, ver) * p;
    }

    /**
     * Método que encuentra el número minimo de carros, partiendo desde el principio de que se va a tratar de encontrar
     * un path más corto al vertice que este más alejado de la empresa a través de otros vértices; una vez agrupado
     * los vértice se eliminan del arreglo de los vertices de la empresa por lo que la complejidad en el peor de los
     * casos es de O(n).
     *
     * @param grafo Grafo completo con la distancia mas corta entre todos los vertices.
     * @param vertexList Lista de ArrayList en los cual contienen cada uno de sus arcos ordenados de menor a major.
     * @return Una lista con las listas con la minima cantidad de carros.
     *
     */
    public static LinkedList<ArrayList<Integer>> generateSolution(AdaptedGraph grafo,LinkedList<ArrayList<Integer>> vertexList, float p){

        LinkedList<ArrayList<Integer>> carrosCompartidos = new LinkedList<>();

        boolean[] visited = new boolean[vertexList.size()];
        int pathCost = 0;

        while(vertexList.get(0).size() > 1){ // Lista de vertices que lleagn a la empresa ordenandos de menor a major.

            int major = vertexList.get(0).get(vertexList.get(0).size() - 1); // Vértice mas alejado de la empresa.

             /**
              * Agrega la lista del posible vehiculo compartido con otros vertices partiendo del vertice más lejos de la empresa
              */
             carrosCompartidos.add(sharedPath(grafo, major, visited, vertexList, pathCost, p));

             /**
              * Elimina los vertices que fueron utilizados en el vehiculo anterior de la lista vertexList con el finalidad de disminur el ciclo.
              */
             for (int i = 0; i < carrosCompartidos.get(carrosCompartidos.size() - 1).size(); i++)
                 vertexList.get(0).remove(vertexList.get(0).indexOf(carrosCompartidos.get(carrosCompartidos.size() - 1).get(i)));

        }

        return carrosCompartidos;
    }

    /**
     * Método que agrupa los vértices que poseen el menor costo hacia otros vértices hasta que legue al tope maximo(5)
     * o hasta que llegue a la univerisada sin contemplar el carro.
     *
     * @param grafo Grafo completo con la distancia mas corta entre todos los vertices.
     * @param major Vértice mas alejado de la empresa.
     * @param visited Arreglo de booleanos(true-> si ya fue visitado o false-> si no ha sido visitado).
     * @param vertexList Lista de ArrayList en los cual contienen cada uno de sus arcos ordenados de menor a major.
     * @param pathCost
     * @return Una lista de vertices
     */
    public static ArrayList<Integer> sharedPath(AdaptedGraph grafo, int major, boolean[] visited, LinkedList<ArrayList<Integer>> vertexList, int pathCost, float p){

        ArrayList<Integer> path = new ArrayList<>();

        int pos = 1;
        float ownerPath = closingTime(grafo, vertexList.get(0).get(vertexList.get(0).size() - 1), p);
        float minorOrigin = ownerPath;

        int pathToMajor = 0;
        int major_origen = 0;

        float pathWithMajor = 0;

        for(int i = 0; i < vertexList.get(0).size(); i++) {

            if (path.size() == 5 && pathCost <= ownerPath) {

                return path;

            } else {

                pathToMajor = grafo.getWeight(major, vertexList.get(major).get(pos));
                pathCost += pathToMajor;
                major_origen = grafo.getWeight(0, vertexList.get(major).get(pos)) + pathToMajor;

                if(major != vertexList.get(major).get(pos) && // para que no se vaya a si mismo
                        pathCost <= ownerPath && //para que no se pase del tiempo maximo del primer vertice
                        !visited[vertexList.get(major).get(pos)] && //para que no vuelva a vertices ya seleccionados
                        0 != vertexList.get(major).get(pos) && //para que no anote que puede llegar a la  universidad
                        major_origen <= minorOrigin) {

                    path.add(major);
                    visited[major] = true;
                    i = 0; // reiniciar el ciclo.

                    major = vertexList.get(major).get(pos);
                    pathWithMajor = closingTime(grafo, major, p);
                    minorOrigin = Math.min(minorOrigin - grafo.getWeight(major, vertexList.get(major).get(pos)), pathWithMajor);

                    pos = 1; //reiniciar la busqueda desde el menor.

                } else {

                    pathCost -= grafo.getWeight(major,vertexList.get(major).get(pos++));
                    // pos++;

                }

            }

        }

        pathCost += grafo.getWeight(0, major);

        if (!visited[major] && pathCost <= ownerPath) {

            path.add(major);
            visited[major] = true;

        }

        return path;
    }

    /**
     * Metodo para escribir un archivo con la respuesta
     * Complejidad: Mejor y peor caso es O(n), donde n son los duenos de vehiculo y la empresa
     *
     * @param  permutations es una lista de listas con la permutacion para cada subconjunto de la particion de duenos de vehiculo
     */
    public static void generateFile(LinkedList<ArrayList<Integer>> permutations,int pointsNumber, float p){

        try {

            PrintWriter escritor = new PrintWriter(
                    "respuesta-ejemplo-U=" + pointsNumber + "-p=" + p + ".txt",
                    "UTF-8");

            for (ArrayList<Integer> permutation : permutations) {

                for (Integer vehicleOwner : permutation)
                    escritor.print((vehicleOwner + 1) + " ");

                escritor.println();

            }

            escritor.close();

        } catch(IOException ioe) {
            System.out.println("Error escribiendo el archivo de salida " + ioe.getMessage() );
        }

    }

    public static void main(String[] args) {

        System.out.println("[ALGORITMO] Iniciando FastPath...");
        System.out.println("[ALGORITMO] Iniciando lecutra de argumentos...");

        //Recibir el numero de duenos de vehiculo y la empresa, y el valor de p por el main
        int pointsNumber = args.length == 0 ? 205 : Integer.parseInt(args[0]); //205
        float latencyTime = args.length < 2 ? 1.3f : Float.parseFloat(args[1]);

        SELECTOR : if(args.length == 0) {

            Object[] option = { "Si", "No" };

            int selection = JOptionPane.showOptionDialog(null,
                    "¿Quieres introducir la cantidad de nodos y el porcentaje de latencia?",
                    "Escoje una opción:",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    option,
                    option[0]);

            if(selection == 0) {

                JTextField uField = new JTextField(5);
                JTextField pField = new JTextField(5);

                JPanel myPanel = new JPanel();

                myPanel.add(new JLabel("Cantidad de Nodos (U):"));
                myPanel.add(uField);
                myPanel.add(Box.createHorizontalStrut(15));

                myPanel.add(new JLabel("Porcentaje de Latencia (p):"));
                myPanel.add(pField);

                int result = JOptionPane.showConfirmDialog(
                        null,
                        myPanel,
                        "Por favor seleccione los atributos del data-set",
                        JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {

                    pointsNumber = Integer.parseInt(uField.getText());
                    latencyTime = Float.parseFloat(pField.getText());

                    System.out.println("[ALGORITMO] La latencia maxima ha sido definida a: " + latencyTime);
                    System.out.println("[ALGORITMO] La cantidad de vertices han sido definidos a: " + pointsNumber);

                }

            }

        }

        System.out.println("[ALGORITMO] Leyendo archivo...");

        // Leer el archivo con las distancias entre los duenos de los vehiculo y la empresa
        AdaptedGraph grafo = readFile(pointsNumber, latencyTime);
        LinkedList<ArrayList<Integer>> vertexList = new LinkedList<>();

        //long startTime = System.currentTimeMillis();
        for (int i = 0; i < grafo.size; i++){

            ArrayList<Integer> succesors = grafo.getSuccessors(i);
            sort(succesors, grafo, i);
            vertexList.add(succesors);

        }

        // Asignar los vehiculos compartidos
        long startTime = System.currentTimeMillis();
        LinkedList<ArrayList<Integer>> permutations = generateSolution(grafo, vertexList, latencyTime); // Iniciar algoritmo

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("[ALGORITMO] El algoritmo tomo un tiempo de: " + estimatedTime + "ms");

        // Imrpimir memoria total consumida.
        System.out.println("[ALGORITMO] Memoria total consumida: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000);

        // Generar archivo de respuesta.
        generateFile(permutations, pointsNumber, latencyTime);

    }

}