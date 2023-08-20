package devlRecord;

import DevlInterface.IComdCons;

/**
 * Используется для консольного ввода команд
 * @param strComd
 * @param iComdCons
 */
public record RecordComdCons(String strComd, IComdCons iComdCons) {}
