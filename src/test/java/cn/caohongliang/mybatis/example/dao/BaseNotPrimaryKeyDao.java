package cn.caohongliang.mybatis.example.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author caohongliang
 */
public interface BaseNotPrimaryKeyDao<ENTITY, EXAMPLE> {
    /**
     * countByExample
     *
     * @param example example
     * @return long
     */
    long countByExample(EXAMPLE example);

    /**
     * deleteByExample
     *
     * @param example example
     * @return int
     */
    int deleteByExample(EXAMPLE example);

    /**
     * 插入
     *
     * @param record record
     * @return int
     */
    int insert(ENTITY record);

    /**
     * 插入, 忽略空字段
     *
     * @param record record
     * @return int
     */
    int insertSelective(ENTITY record);

    /**
     * selectByExample
     *
     * @param example example
     * @return list
     */
    List<ENTITY> selectByExample(EXAMPLE example);

    /**
     * example查询，返回一个
     * @param example
     * @return
     */
    default Optional<ENTITY> selectOneByExample(EXAMPLE example){
        List<ENTITY> entities = this.selectByExample(example);
        //TODO 2019/4/17 这里可以抛异常
        return entities.isEmpty() ? Optional.empty() : Optional.of(entities.get(0));
    }


    /**
     * updateByExampleSelective
     *
     * @param record  record
     * @param example example
     * @return int
     */
    int updateByExampleSelective(@Param("record") ENTITY record, @Param("example") EXAMPLE example);

    /**
     * updateByExample
     *
     * @param record  record
     * @param example example
     * @return int
     */
    int updateByExample(@Param("record") ENTITY record, @Param("example") EXAMPLE example);
}
