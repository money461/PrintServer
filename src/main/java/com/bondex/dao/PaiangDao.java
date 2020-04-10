package com.bondex.dao;

import java.util.List;

import com.bondex.entity.Label;

public interface PaiangDao {

	void savePaiangData(List<Label> list);

	void updatePaiangData(List<Label> datalist);

	void delete(List<Label> list);

}
