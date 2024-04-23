<template>
  <a-select v-model:value="customerId" show-search :filterOption="filterNameOption" :disabled="modelSwitch" :style="'width:' + modelWidth" @change ="onChange" placeholder="请选择信托机构名称">
    <a-select-option v-for="item in CUSTOMER_NAME_ARRAY" :key="item.code" :value="item.code" :label="item.description">
      {{item.description}}
    </a-select-option>
  </a-select>
</template>

<script>
import {defineComponent, onMounted, ref, watch} from "vue";
import axios from "axios";
export default defineComponent({
  name: "custodian-customer-select-view",
  // 定义子父组件数据交互变量，modelValue为默认定义交换v-model所绑定的值
  props:["modelValue", "width", "disabled"],
  // 定义事件名称
  emits:["update:modelValue"],
  setup(props, {emit}){

    const customerId = ref();
    const modelSwitch = ref(props.disabled);
    const modeWidth = ref(props.width);
    const CUSTOMER_NAME_ARRAY = ref([]);

    // 监控调用该组件的其他组件v-model所绑定的值，并将值赋给name；
    watch(() => props.modelValue, () => {
      customerId.value = props.modelValue;
    },{immediate : true})

    watch(() => props.disabled, () => {
      modelSwitch.value = props.disabled;
    },{immediate : true})

    /**
     * 当子组件数据发生变化时，将对应的值传递给调用其的父组件v-model所绑定的变量
     * @param value
     */
    const onChange = (value) => {
      emit('update:modelValue', value);
    }

    if(window.Tool.isEmpty(props.width)){
      modeWidth.value = "100%";
    }

    const handleQueryOptions = () => {
      axios.get("/cyb/customers/query-options").then((response) => {
        const json = response.data;
        if (json.status) {
          CUSTOMER_NAME_ARRAY.value = json.data;
        }
      });
    }

    const filterNameOption = (input, option) => {
      return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    };

    onMounted(() => {
      handleQueryOptions();
    });

    return {
      customerId,
      modeWidth,
      filterNameOption,
      onChange,
      CUSTOMER_NAME_ARRAY,
      modelSwitch

    }
  }
})
</script>

<style scoped>

</style>