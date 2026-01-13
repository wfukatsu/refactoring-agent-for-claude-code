import { createSlice } from "@reduxjs/toolkit";
import { convertMapToList } from "../../utils/constant";

const initialState = {
  itemDetails: null,
  allowedList: [],
  deniedList: [],
  auditSetList: [],
  selectedAuditSet: "null",

  orgDeniedList: [],
  isLoading: false,
};

const folderSlice = createSlice({
  name: "auditFolder",
  initialState,
  reducers: {
    newaddToDenaiList: {
      reducer(state, action) {
        const stateDataMap = new Map();
        [...state.allowedList].forEach((item) => {
          stateDataMap.set(item.id, item);
        });
        let data = action.payload.data;
        [...data].forEach((item) => {
          stateDataMap.set(item.id, item);
        });

        const list = [];
        stateDataMap.forEach((value, key) => {
          list.push(value);
        });

        state.allowedList = [...list];
      },
      prepare(items) {
        return { payload: { data: [...items] } };
      },
    },
    newremoveFromDenaiList: {
      reducer(state, action) {
        const stateDataMap = new Map();
        [...state.allowedList].forEach((item) => {
          stateDataMap.set(item.id, item);
        });
        
        let data = action.payload.data;

        [...data].forEach((item) => {
          if (stateDataMap.has(item.id)) {
            stateDataMap.delete(item.id);
          }
        });

        const list = convertMapToList(stateDataMap);

        state.allowedList = [...list];
        
      },
      prepare(items) {
        return { payload: { data: [...items] } };
      },
    },

    toggleLoading: {
      reducer(state) {
        state.isLoading = !state.isLoading;
      },
      prepare() {
        return { payload: {} };
      },
    },
    updateAuditSet: {
      reducer(state, action) {
        state.auditSetList = action.payload.auditSetList;
      },
      prepare(auditSetList) {
        return { payload: { auditSetList } };
      },
    },
    setItemDetails: {
      reducer(state, action) {
        state.itemDetails = action.payload;
      },
      prepare(item) {
        return { payload: {} };
      },
    },
    updateSelectedAuditSet: {
      reducer(state, action) {
        state.selectedAuditSet = action.payload.selectedAuditSet;
        state.allowedList = [...action.payload.allowedList];
        state.orgDeniedList = [...action.payload.allowedList];
      },
      prepare(allowedList, selectedAuditSet) {
        return {
          payload: { allowedList, selectedAuditSet },
        };
      },
    },
    toggleAllowedList: {
      reducer(state, action) {
        let item = action.payload.item;
        let toggle = action.payload.toggle;
        if (toggle) {
          state.allowedList.push({
            itemId: item.id,
            itemType: item.itemType,
            denyItems: [],
          });
        } else {
          state.allowedList = state.allowedList.filter(
            (i) => i.itemId !== item.id
          );
        }
      },
      prepare(item, toggle) {
        return {
          payload: { item, toggle },
        };
      },
    },
    toggleAllowedListFromRoot: {
      reducer(state, action) {
        let items = action.payload.itemDetails;
        let toggle = action.payload.toggle;
        if (toggle) {
          state.allowedList = [];
          items.forEach((item) => {
            state.allowedList.push({
              itemId: item.id,
              itemType: item.itemType,
              denyItems: [],
            });
          });
        } else {
          state.allowedList = [];
        }
      },
      prepare(itemDetails, toggle) {
        return {
          payload: { itemDetails, toggle },
        };
      },
    },
    addToDenaiList: {
      reducer(state, action) {
        const item = action.payload.item;
        const subRootItemId = action.payload.subRootItemId;
        const subRoot = state.allowedList.find(
          (item) => item.itemId === subRootItemId
        );

        if (subRoot) {
          subRoot.denyItems.push({
            itemId: item.id,
            itemType: item.itemType,
          });
          state.allowedList = state.allowedList.filter((i) => {
            return i.itemId !== subRootItemId;
          });
          state.allowedList.push(subRoot);
        }
      },
      prepare(item, subRootItemId) {
        return { payload: { item, subRootItemId } };
      },
    },
    removeFromDenaiList: {
      reducer(state, action) {
        const item = action.payload.item;
        const subRootItemId = action.payload.subRootItemId;
        const subRoot = state.allowedList.find(
          (item) => item.itemId === subRootItemId
        );

        if (subRoot) {
          subRoot.denyItems = subRoot.denyItems.filter((i) => {
            return i.itemId !== item.id;
          });
          state.allowedList = state.allowedList.filter((i) => {
            return i.itemId !== subRootItemId;
          });
          state.allowedList.push(subRoot);
        }
      },
      prepare(item, subRootItemId) {
        return { payload: { item, subRootItemId } };
      },
    },
    toggleDenaiList(state, action) {
      let itemId = action.payload.id;
      let itemType = action.payload.itemType;
      let ids = action.payload.ids;

      if (state.deniedList.find((obj) => obj.itemId === itemId)) {
        state.deniedList = state.deniedList.filter(
          (item) => item.itemId !== itemId
        );
      } else {
        state.deniedList = [{ itemId, itemType }, ...state.deniedList];

        [...ids].forEach((id) => {
          if (state.deniedList.find((obj) => obj.itemId === id)) {
            state.deniedList = state.deniedList.filter(
              (item) => item.itemId !== id
            );
          }
        });
      }
    },
    updateDenaiList: {
      reducer(state, action) {
        state.allowedList = [...action.payload.allowedList];
        state.orgDeniedList = [...action.payload.allowedList];
      },
      prepare(allowedList) {
        return { payload: { allowedList } };
      },
    },
  },
});

export const {
  toggleLoading,
  setItemDetails,
  addToDenaiList,
  removeFromDenaiList,
  updateAuditSet,
  updateSelectedAuditSet,
  toggleDenaiList,
  updateDenaiList,
  toggleAllowedList,
  toggleAllowedListFromRoot,
  newaddToDenaiList,
  newremoveFromDenaiList,
} = folderSlice.actions;

export default folderSlice.reducer;
