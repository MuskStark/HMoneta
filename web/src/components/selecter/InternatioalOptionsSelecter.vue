<template>
  <a-select v-model:value="optionId" show-search :filterOption="filterNameOption" :disabled="modelSwitch" :style="'width:' + modelWidth" @change ="onChange" placeholder="请选择业务名称">
    <a-select-option v-for="item in INTERNATIONAL_OPTIONS_ARRAY" :key="item.code" :value="item.code" :label="item.description">
      {{item.description}}
    </a-select-option>
  </a-select>
</template>

<script>
import {defineComponent, onMounted, ref, watch} from "vue";
import axios from "axios";
export default defineComponent({
  name: "international-options-select-view",
  // 定义子父组件数据交互变量，modelValue为默认定义交换v-model所绑定的值
  props:["modelValue", "width", "disabled"],
  // 定义事件名称
  emits:["update:modelValue"],
  setup(props, {emit}){

    const optionId = ref();
    const modelSwitch = ref(props.disabled);
    const modeWidth = ref(props.width);
    const INTERNATIONAL_OPTIONS_ARRAY = ref([]);

    // 监控调用该组件的其他组件v-model所绑定的值，并将值赋给name；
    watch(() => props.modelValue, () => {
      optionId.value = props.modelValue;
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
      axios.get("/in/options/query-all").then((response) => {
        const json = response.data;
        if (json.status) {
          INTERNATIONAL_OPTIONS_ARRAY.value = json.data;
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
      optionId,
      modeWidth,
      filterNameOption,
      onChange,
      INTERNATIONAL_OPTIONS_ARRAY,
      modelSwitch

    }
  }
})
</script>

<style scoped>

</style>