package com.company; // указываем что классы принадлежат пакету company

import java.io.*; // библиотека для работы с потоками данных
import java.nio.file.Files; // библиотека для работы с файлами
import java.nio.file.Paths; // библиотека для работы с путями к файлам
import java.util.ArrayList; // библиотека для работы с массивами
import java.util.Collections; // библиотека для работы с коллекциями
import java.util.Map; // библиотка для работы с деревьями
import java.util.TreeMap; // библиотека для работы с деревьями

/* Главный класс */
public class Main {

    /* Главная функция */
    public static void main(String[] args) {
	    String text = "Везет Сенька Саньку с Сонькой на санках. Санки скок, Сеньку с ног, Соньку в лоб, все - в сугроб."; // исходная текстовая строка
	    TreeMap<Character, Integer> frequencies = countFrequency(text); // коллекция частотности
        ArrayList<CodeTreeNode> codeTreeNodes = new ArrayList<>(); // создаем список узлов для листов дерева
        System.out.println("ПРИМЕР РАБОТЫ АЛГОРИТМА НА МАЛЕНЬКОМ ТЕКСТЕ:");
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Исходный текст: \n" + text); // выводим исходный текст
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Частотный анализ текста: ");
        System.out.println( frequencies ); // выводим частотный анализ текста
        System.out.println("-----------------------------------------------------------------------");
        for (Character c: frequencies.keySet()) { // цикл по всем символам из таблицы
            codeTreeNodes.add(new CodeTreeNode(c, frequencies.get(c))); // добавляем новый узел в список (символ и его частота)
        }
        CodeTreeNode tree = huffman(codeTreeNodes); // строим кодовое дерево из массива узлов с помощью алгоритма Хаффмана
        TreeMap<Character, String> codes = new TreeMap<>(); // создаем коллекцию кодов
        for(Character c: frequencies.keySet()) { // цикл по всем символам
            codes.put(c, tree.getCodeForCharacter(c, "")); // добавлям для каждого символа его код
        }
        System.out.println("Таблица префиксных кодов: \n" + codes.toString()); // выводим таблицу префиксных кодов
        System.out.println("-----------------------------------------------------------------------");
        StringBuilder encoded = new StringBuilder(); // создаем строку содержащую сжатые биты
        for (int i=0; i<text.length(); i++) { // цикл по строке
            encoded.append(codes.get(text.charAt(i))); // код каждого символа записываем в строку содержащую сжатые биты
        }
        System.out.println("Размер исходной строки: \n" + text.getBytes().length * 8 + " бит"); // выводим размер исходной строки
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Размер сжатой строки: \n" + encoded.length() + " бит"); // выводим размер сжатой строки
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Биты сжатой строки: \n" + encoded); // выводим биты сжатой строки
        System.out.println("-----------------------------------------------------------------------");
        String decoded = huffmanDecode(encoded.toString(), tree); // декодируем сжатую строку
        System.out.println("Текст после декомпрессии: \n" + decoded); // выводим расшифрованную строку
        System.out.println("-----------------------------------------------------------------------\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        fileCompressTest();
    }

    /* Функция считающая сколько раз каждый символ встречается в тексте <Символ, Частота> */
    private static TreeMap<Character, Integer> countFrequency(String text) {
        TreeMap<Character, Integer> freqMap = new TreeMap<>(); // инициализируем коллекцию частотности
        for(int i = 0; i < text.length(); i++) { // цикл по исходному тексту
            Character c = text.charAt(i); // записываем символ находящийся по текущему индексу
            Integer count = freqMap.get(c); // создаем счетчик по текущему символу
            freqMap.put(c, count != null ? count + 1 : 1); // добавляем запись в дерево
        }
        return freqMap; // возвращаем полученное дерево
    }

    /* Функция алгоритма хафмана (возвращает дерево, принимает список узлов) */
    private static CodeTreeNode huffman(ArrayList<CodeTreeNode> codeTreeNodes) {
        while (codeTreeNodes.size() > 1) { // пока в списке узлов есть узлы
            Collections.sort(codeTreeNodes); // упорядочиваем узлы по убыванию
            CodeTreeNode left = codeTreeNodes.remove(codeTreeNodes.size() - 1); // создаем узел с самым маленьким весом и удаляем его из массива
            CodeTreeNode right = codeTreeNodes.remove(codeTreeNodes.size() - 1); // создаем узел с самым маленьким весом и удаляем его из массива
            CodeTreeNode parent = new CodeTreeNode(null, right.weight + left.weight, left, right); // создаем промежуточный узел для двух предыдущих (его вес равен сумме весов потомков)
            codeTreeNodes.add(parent); // добавляем узел промежуточный узел обратно в массив
        }
        return  codeTreeNodes.get(0); // возвращаем корневой узел который в итоге получился
    }
    /* Функция декодирующая строку с битами (строка с битами, кодовое дерево) */
    private static String huffmanDecode(String encoded, CodeTreeNode tree) {
        StringBuilder decoded = new StringBuilder(); // строка в которой накапливаем расшифрованные данные
        CodeTreeNode node = tree; // переменная в которой храним текущий узел (изначально равен корневому узлу)
        for(int i=0; i < encoded.length(); i++) { // цикл по битам зашифрованной строки
            node = encoded.charAt(i) == '0' ? node.left : node.right; // если текущий бит 0, то идем налево, иначе направо
            if(node.content != null) { // если мы дошли до листа дерева
                decoded.append(node.content); // добавляем в декодированную последовательность символ содержащийся в листе
                node = tree; // возвращаемся в корень дерева
            }
        }
        return decoded.toString(); // возвращаем расшифрованную строку
    }

    /* Класс для представления узла дерева (содержит интерфейс сравнения) */
    private static class CodeTreeNode implements Comparable<CodeTreeNode> {
        Character content; // символ
        int weight; // частота (для узла) или сумма частот дочерних узлов (для промежуточного узла)
        CodeTreeNode left; // левый потомок
        CodeTreeNode right; // правый потомок

        /* Конструктор с параметрами */
        public CodeTreeNode(Character content, int weight) {
            this.content = content; // символ
            this.weight = weight; // частота
        }

        /* Конструктор с параметрами */
        public CodeTreeNode(Character content, int weight, CodeTreeNode left, CodeTreeNode right) {
            this.content = content; // символ
            this.weight = weight; // частота
            this.left = left; // левый потомок
            this.right = right; // правый потомок
        }

        /* Встроенная в интерфейс функция сравнения */
        @Override
        public int compareTo(CodeTreeNode o) {
            return o.weight - weight; // у кого частота больше тот стоит на первом месте (сортировка по убыванию)
        }

        /* Функция делающая поиск нужного символа в дереве рекурсивным обходом (искомый символ, путь) */
        public String getCodeForCharacter(Character ch, String parentPath) {
            if (content == ch) { // если контентом текущего узла является символ
                return parentPath; // возвращаем код текущего символа
            } else { // если контентом текущего узла является не символ
                if (left != null) { // если у текущего узла есть левый потомок
                    String path = left.getCodeForCharacter(ch, parentPath + 0); // рекурсивно вызываем функцию и дописываем в пути 0
                    if (path != null) { // если в левом поддереве данного узла есть нужный символ
                        return path; // возвращаем его путь
                    }
                }
                if (right != null) { // если у текущего узла есть правый потомок
                    String path = right.getCodeForCharacter(ch, parentPath + 1); // рекурсивно вызываем функцию и дописываем в пути 1
                    if (path != null) { // если в левом поддереве данного узла есть нужный символ
                        return path; // возвращаем его путь
                    }
                }
            }
            return null; // в данном поддереве ничего не найдено
        }
    }

    /* Класс реализующий битовый массив (для группировки битов в байты и сохранения их в файл) */
    public static class BitArray {
        int size; // размер массива
        byte[] bytes; // байты массива

        /* Массив масок для работы с отдельными разрядами */
        private byte[] masks = new byte[] {0b00000001, 0b00000010, 0b00000100, 0b00001000,
                0b00010000, 0b00100000, 0b01000000, (byte) 0b10000000};

        /* Конструктор с параметрами (принимает размер) */
        public BitArray(int size) {
            this.size = size; // размер
            int sizeInBytes = size / 8; // вычисление сколько нужно байтов чтобы поместить в себя все данные биты
            if (size % 8 > 0) { // если размер не кратен восьми
                sizeInBytes = sizeInBytes + 1; // выделяем дополнительный байт для хранения остатка
            }
            bytes = new byte[sizeInBytes]; // создаем массив байт в котором хранятся байты
        }

        /* Конструктор с параметрами (принимаем размер и байты) */
        public BitArray(int size, byte[] bytes) {
            this.size = size; // размер
            this.bytes = bytes; // байты
        }

        /* Функция получения значения бита из массива по индексу */
        public int get(int index) {
            int byteIndex = index / 8; // вычисление номера байта
            int bitIndex = index % 8; // вычисление номера бита
            /* Возвращаем значение по индексу */
            return (bytes[byteIndex] & masks[bitIndex]) != 0 ? 1 : 0; // если возвращенное число не равно нулю, то значит там 1, иначе 0
        }

        /* Функция задания значения бита (индекс, значение) */
        public void set(int index, int value) {
            int byteIndex = index / 8; // вычисление номера байта
            int bitIndex = index % 8; // вычисление номера бита
            if (value != 0) { // если значение не ноль
                bytes[byteIndex] = (byte) (bytes[byteIndex] | masks[bitIndex]); // устанавливаем единицу в бит по индексу
            } else { // иначе
                bytes[byteIndex] = (byte) (bytes[byteIndex] & ~masks[bitIndex]); // устанавливаем ноль в бит по индексу
            }
        }

        /* Переопределяем функцию toString */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(); // создаем строку
            for (int i = 0; i < size; i++) { // идем по всем битам
                sb.append(get(i) > 0 ? '1' : '0'); // добавляем в строку соответствующий бит
            }
            return sb.toString(); // возвращаем строку
        }

        /* Функция возвращающая размер (в байтах) */
        public int getSizeInBytes() {
            return bytes.length; // возвращаем размер байтов
        }
    }

    /* Функция сохранения таблицы частот и сжатой информации в файл */
    private static void saveToFile(File output, Map<Character, Integer> frequencies, String bits) {
        try {
            DataOutputStream os = new DataOutputStream(new FileOutputStream(output)); // создаем поток вывода
            os.writeInt(frequencies.size()); // записываем размер таблицы частот в поток вывода
            for (Character character: frequencies.keySet()) { // цикл по таблице частот
                os.writeChar(character); // записываем символ в поток
                os.writeInt(frequencies.get(character)); // записываем в поток то сколько раз текущий символ встречается в тексте
            }
            int compressedSizeBits = bits.length(); // записываем в поток вывода размер зашифрованных данных в битах
            BitArray bitArray = new BitArray(compressedSizeBits); // создаем массив битов
            for (int i = 0; i < bits.length(); i++) {  // цикл по битам
                bitArray.set(i, bits.charAt(i) != '0' ? 1 : 0); // записываем бит в массив битов
            }
            os.writeInt(compressedSizeBits); // записываем биты в поток вывода
            os.write(bitArray.bytes, 0, bitArray.getSizeInBytes()); // записываем байты в поток вывода
            os.flush(); // очищаем поток вывода
            os.close(); // закрываем поток вывода

        } catch (FileNotFoundException e) { // если ошибка
            e.printStackTrace(); // выводим её
        } catch (IOException e) { // если ошибка
            e.printStackTrace(); // выводим её
        }
    }

    /* загрузка сжатой информации и таблицы частот из файла */
    private static void loadFromFile(File input, Map<Character, Integer> frequencies, StringBuilder bits) {
        try {
            DataInputStream os = new DataInputStream(new FileInputStream(input));  // создаем поток ввода
            int frequencyTableSize = os.readInt(); // считываем размер таблицы
            for (int i = 0; i < frequencyTableSize; i++) { // цикл по таблице
                frequencies.put(os.readChar(), os.readInt()); // считываем таблицу
            }
            int dataSizeBits = os.readInt(); // считываем размер данных в битах
            BitArray bitArray = new BitArray(dataSizeBits); // создаем массив битов
            os.read(bitArray.bytes, 0, bitArray.getSizeInBytes()); // считываем данные с файла
            os.close(); // закрываем поток ввода

            for (int i = 0; i < bitArray.size; i++) { // цикл по массиву битов
                bits.append(bitArray.get(i) != 0 ? "1" : 0); // записываем массив битов в строку битов
            }

        } catch (FileNotFoundException e) { // если ошибка
            e.printStackTrace(); // выводим её
        } catch (IOException e) { // если ошибка
            e.printStackTrace(); // выводим её
        }
    }

    /* Проверка алгоритма сжатия на текстовом файле */
    private static void fileCompressTest() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("input.txt"))); // загрузка содержимого файла в виде строки
            TreeMap<Character, Integer> frequencies = countFrequency(content); // вычисление таблицы частот
            ArrayList<CodeTreeNode> codeTreeNodes = new ArrayList<>(); // создаем список узлов для листов дерева
            for(Character c: frequencies.keySet()) { // цикл по всем символам из таблицы
                codeTreeNodes.add(new CodeTreeNode(c, frequencies.get(c))); // добавляем новый узел в список (символ и его частота)
            }
            CodeTreeNode tree = huffman(codeTreeNodes); // построение оптимального кодового дерева алгоритмом Хаффмана
            TreeMap<Character, String> codes = new TreeMap<>(); // создаем коллекцию кодов
            for (Character c: frequencies.keySet()) { // цикл по всем символам
                codes.put(c, tree.getCodeForCharacter(c, "")); // добавляем для каждого символа свой код
            }
            StringBuilder encoded = new StringBuilder(); // создаем строку содержащую сжатые биты
            for (int i = 0; i < content.length(); i++) { // цикл по строке
                encoded.append(codes.get(content.charAt(i))); // код каждого символа записываем в строку содержащую сжатые биты
            }
            File file = new File("compressed.huf"); // обозначаем файл для сохранения информации
            saveToFile(file, frequencies, encoded.toString()); // сохраняем информацию в файл
            TreeMap<Character, Integer> frequencies2 = new TreeMap<>(); // создаем таблицу частот из файла
            StringBuilder encoded2 = new StringBuilder(); // создаем строку содержащую сжатые биты
            codeTreeNodes.clear(); // очищаем список узлов для листа дерева
            loadFromFile(file, frequencies2, encoded2); // извлекаем сжатую информацию из файла
            /* Генерация листов и постоение кодового дерева Хаффмана на основе таблицы частот сжатого файла */
            for(Character c: frequencies2.keySet()) { // цикл по таблице частот
                codeTreeNodes.add(new CodeTreeNode(c, frequencies2.get(c))); // построение листов по таблице частот
            }
            CodeTreeNode tree2 = huffman(codeTreeNodes); // построение кодового дерева по листам
            String decoded = huffmanDecode(encoded2.toString(), tree2); // декодирование обратно исходной информации из сжатой
            Files.write(Paths.get("decompressed.txt"), decoded.getBytes()); // сохранение в файл декодированной информации
            System.out.println("РАБОТА С БОЛЬШИМ ТЕКСТОМ:");
            System.out.println("-----------------------------------------------------------------------");
            System.out.println("Частотный анализ текста:");
            System.out.println( frequencies2 ); // выводим частотный анализ текста
            System.out.println("-----------------------------------------------------------------------");
        } catch (IOException e) { // если ошибка
            e.printStackTrace(); // выводим её
        }
    }
}