import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**********************************************************************************************************************************************
 *   APLICACIÓN: "MediaForkJoinRecursivo"                                                                                                     *
 **********************************************************************************************************************************************
 *   PROGRAMACIÓN DE SERVICIOS Y PROCESOS 2DAM  -  Eclipse IDE for Java Developers v2021-09 (4.21.0)                                          *
 **********************************************************************************************************************************************
 *   @author D.Lacasa                                                                                                                         *
 *   @version 2.0 - Versión inicial del algoritmo.                                                                                            *
 *   @since 15NOV2023                                                                                                                         *
 **********************************************************************************************************************************************
 *   COMENTARIOS:                                                                                                                             *
 *      - Separa un array en dos mitades para despus calcular la media de cada una                                                            *                                                                                                                                     *
 **********************************************************************************************************************************************/

public class MediaForkJoinRecursivo extends RecursiveTask<Double>
{
    // Constantes para definir límites y umbrales
    private static final int LIMITE_ARRAY = 1000;
    private static final int UMBRAL = 100;

    // Variables para el array y los límites
    private short[] a_Vector = null;
    private int a_Inicio, a_Fin = 0;

    // Constructores para inicializar la tarea con el array y límites
    MediaForkJoinRecursivo() { }

    MediaForkJoinRecursivo(short[] p_Vector, int p_Inicio, int p_Fin)
    {
        this.a_Vector = p_Vector;
        this.a_Inicio = p_Inicio;
        this.a_Fin = p_Fin;
    }   // MediaForkJoinRecursivo()

    // Método para calcular la media de manera recursiva
    private Double getHalfRec()
    {
        int l_Medio = (a_Inicio + a_Fin) / 2;

        // Se crean dos tareas para calcular las medias de cada mitad
        MediaForkJoinRecursivo l_Tarea1 = new MediaForkJoinRecursivo(a_Vector, a_Inicio, l_Medio);
        MediaForkJoinRecursivo l_Tarea2 = new MediaForkJoinRecursivo(a_Vector, l_Medio, a_Fin);

        // Se ejecutan las tareas en paralelo
        l_Tarea1.fork();
        l_Tarea2.fork();

        // Se obtienen los resultados y se calcula la media total
        Double l_Resultado1 = l_Tarea1.join();
        Double l_Resultado2 = l_Tarea2.join();

        return (l_Resultado1 + l_Resultado2) / 2.0;
    }   // getHalfRec()

    // Método para calcular la media secuencialmete
    private Double getHalfSec()
    {
        double l_Media = 0.0;

        // Se sumacada elemento del array para calcular la media
        for (int l_Contador = a_Inicio; l_Contador < a_Fin; l_Contador++) {
            l_Media += a_Vector[l_Contador];
        }
        l_Media /= (a_Fin - a_Inicio);

        return l_Media;
    }   // getHalfSec()

    // Método que implementa el cálculo de la media
    @Override
    protected Double compute()
    {
        // Decide si realizar el cálculo de manera secuancial o recursiva
        if (a_Fin - a_Inicio <= UMBRAL) {
            return getHalfSec();
        } else {
            return getHalfRec();
        }
    }   // compute()

    // Método para crear un array de cierta longitud e inicializarlo
    public short[] crearArray(int p_Longitud)
    {
        short[] l_Array = new short[p_Longitud];
        for (int l_Contador = 0; l_Contador < p_Longitud; l_Contador++) {
            l_Array[l_Contador] = (short) l_Contador;
        }
        return l_Array;
    }   // crearArray()

    // Método principal donde se ejecuta la lóica del programa
    public static void main(String[] args)
    {
        // Se crea una tarea y un array de números
        MediaForkJoinRecursivo l_Tarea = new MediaForkJoinRecursivo();
        short[] l_Data = l_Tarea.crearArray(LIMITE_ARRAY);
        int l_Inicio = 0;
        int l_Fin = l_Data.length;
        double l_ResultadoInvoke;
        double l_ResultadoJoin;
        ForkJoinPool l_Pool = new ForkJoinPool();

        // Se inicializa la tarea con los datos y se ejecuta para obtener la media con invoke y join
        l_Tarea = new MediaForkJoinRecursivo(l_Data, l_Inicio, l_Fin);
        l_ResultadoInvoke = l_Pool.invoke(l_Tarea);
        l_ResultadoJoin = l_Tarea.join();

        // Se imprimen los resultados obtenidos
        System.out.println("Media segun invoke: " + l_ResultadoInvoke);
        System.out.println("Media segun join: " + l_ResultadoJoin);

        // Se cierra el pool
        l_Pool.close();
    }   // main()
}   // MediaForkJoinRecursivo