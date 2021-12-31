import request from "@/utils/request";

export function getList(params) {
  return request({
    url: "/mapper",
    method: "get",
    params,
  });
}

export function getXmlDetail(params) {
  return request({
    url: "/mapper/detail",
    method: "get",
    params: {xmlId:params},
  });
}

export function submitXmlInfo(data) {
  return request({
    url: "/mapper/",
    method: "put",
    data,
  });
}
