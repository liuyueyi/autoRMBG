import os
import sys

import torch
from PIL import Image
from huggingface_hub import hf_hub_download
from skimage import io

from briarmbg import BriaRMBG
from utilities import preprocess_image, postprocess_image


def example_inference(input_img: str, img_type="jpg", out_img_suffix="_no_bg.png"):
    model_path = hf_hub_download("briaai/RMBG-1.4", 'model.pth')
    print("model_path ==> ", model_path)
    im_path = f"{os.path.dirname(os.path.abspath(__file__))}/resource/{input_img}.{img_type}"

    net = BriaRMBG()
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    net.load_state_dict(torch.load(model_path, map_location=device))
    net.to(device)
    net.eval()

    # prepare input
    model_input_size = [1024, 1024]
    orig_im = io.imread(im_path)
    orig_im_size = orig_im.shape[0:2]
    image = preprocess_image(orig_im, model_input_size).to(device)

    # inference 
    result = net(image)

    # post process
    result_image = postprocess_image(result[0][0], orig_im_size)

    # save result
    pil_im = Image.fromarray(result_image)
    no_bg_image = Image.new("RGBA", pil_im.size, (0, 0, 0, 0))
    orig_image = Image.open(im_path)
    no_bg_image.paste(orig_image, mask=pil_im)
    save_file_name = f"{os.path.dirname(os.path.abspath(__file__))}/output/{input_img}{out_img_suffix}"
    no_bg_image.save(save_file_name)
    print("图片处理完成!")
    return save_file_name


if __name__ == "__main__":
    # # 命令行方式执行
    # # .\venv\Scripts\python.exe .\example_inference.py dog jpg oo
    # argv = sys.argv
    # file_name = argv[1]
    # if len(argv) > 2:
    #     file_type = argv[2]
    # else:
    #     file_type = "jpg"

    # if len(argv) > 3:
    #     out_suffix = argv[3]
    # else:
    #     out_suffix = "_no_bg"
    # print("命令行方式执行!")
    # example_inference(file_name, file_type, out_suffix)

    example_inference("giraffe")
