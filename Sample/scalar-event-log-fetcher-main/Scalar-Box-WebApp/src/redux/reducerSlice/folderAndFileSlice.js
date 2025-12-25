import { createSlice } from "@reduxjs/toolkit";

const INITIAL_STATE = {
  data: [],
  subFolder: [],
};

const folderAndFileSlice = createSlice({
  name: "folderAndFileSlice",
  initialState: INITIAL_STATE,
  reducers: {
    add: {
      reducer(state, action) {
        const rootId = action.payload.rootId;
        const childrens = action.payload.childrens;
        console.log("folderAndFileSlice bbb", rootId, childrens);
        const stateMap = new Map();

        stateMap.set(rootId, childrens);

        state.data.forEach((item) => {
          console.log("LIST : ", item.rootId);
          stateMap.set(item.rootId, item.childrens);
        });

        const list = [];
        stateMap.forEach((value, key) => {
          list.push({ rootId: key, childrens: value });
          console.log("LIST KEY : ", key, "VALUE : ", value);
        });
        state.data = [...list];
      },
      prepare(rootId, childrens) {
        return {
          payload: {
            rootId,
            childrens,
          },
        };
      },
    },
    addSubFolder: {
      reducer(state, action) {
        const parentNodeId = action.payload.parentNodeId;
        const currentNodeId = action.payload.currentNodeId;

        const childrens = action.payload.childrens;
        console.log("folderAndFileSlice bbb", parentNodeId, childrens);
        const stateMap = new Map();

        stateMap.set(`${parentNodeId}-${currentNodeId}`, childrens);

        state.subFolder.forEach((item) => {
          console.log("LIST : ", item);
          stateMap.set(item.key, item.childrens);
        });

        const list = [];
        stateMap.forEach((value, key) => {
          list.push({ key: key, childrens: value });
          console.log("LIST KEY : ", key, "VALUE : ", value);
        });
        state.subFolder = [...list];
      },
      prepare(parentNodeId, currentNodeId, childrens) {
        return {
          payload: {
            parentNodeId,
            currentNodeId,
            childrens,
          },
        };
      },
    },
    reset: {
      reducer(state, action) {
        state.data = [];
        state.subFolder = [];
      },
      prepare() {
        return {
          payload: {},
        };
      },
    },
  },
});

export const { add, reset, addSubFolder } = folderAndFileSlice.actions;
export default folderAndFileSlice.reducer;
